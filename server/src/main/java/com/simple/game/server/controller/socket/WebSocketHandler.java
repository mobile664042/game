package com.simple.game.server.controller.socket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.simple.game.core.domain.cmd.rtn.game.InvalidateSesssionRtnCmd;
import com.simple.game.core.domain.cmd.rtn.game.SysRtnCmd;
import com.simple.game.core.util.GameSession;
import com.simple.game.server.constant.MyConstant;
import com.simple.game.server.filter.OnlineAccount;
import com.simple.game.server.filter.OnlineAccount.GameOnlineInfo;
import com.simple.game.server.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebSocketHandler {
	/***
	 * 已连接的webSocket(游戏)
	 * key 		gameCode--webSocketId
	 * value 	GameSession
	 */
	private final ConcurrentHashMap<String, GameSession> onlineSessionMap = new ConcurrentHashMap<String, GameSession>();
	
	private final String supportGameCode = MyConstant.DDZ;
	
	/***
	 * 贪图方便，借用
	 */
	@Autowired
    private UserService userService;
	
	@Autowired
	private WebGameDispatcher webGameDispatcher;

    public void onOpen(Session session, String gameCode, String loginToken) {
    	if(!supportGameCode.equals(gameCode)) {
    		log.warn("不好意思，还不支持{}游戏, loginToken={}", gameCode, loginToken);
    		try {
    			SysRtnCmd rtnCmd = SysRtnCmd.build(String.format("还不支持{}游戏", gameCode));
				session.getBasicRemote().sendText(JSON.toJSONString(rtnCmd));
			} catch (IOException e) {
			}
    		return ;
    	}
    	
    	OnlineAccount onlineAccount = userService.getOnlineAccount(loginToken);
    	if(onlineAccount == null) {
    		log.warn("不好意思，loginToken={}已失效", loginToken);
    		try {
    			InvalidateSesssionRtnCmd rtnCmd = InvalidateSesssionRtnCmd.build();
				session.getBasicRemote().sendText(JSON.toJSONString(rtnCmd));
			} catch (IOException e) {
			}
    		return; 
    	}
    	
    	GameSession gameSession = new MyGameSession(session);
    	gameSession.getAttachment().put(MyConstant.PLAYER_ID, onlineAccount.getUser().getId());
    	gameSession.getAttachment().put(MyConstant.NICKNAME, onlineAccount.getUser().getNickname());
    	gameSession.getAttachment().put(MyConstant.SEX, onlineAccount.getUser().getSex());
    	gameSession.getAttachment().put(MyConstant.TELPHONE, onlineAccount.getUser().getTelphone());
    	gameSession.getAttachment().put(MyConstant.HEADPIC, onlineAccount.getUser().getHeadPic());
    	
    	try {
    		GameOnlineInfo old = onlineAccount.getOnlineWebSocket().get(gameCode);
    		if(old != null) {
    			old.setSession(session);
    			webGameDispatcher.onReOpen(gameCode, gameSession);
    		}
    		else {
    			GameOnlineInfo gameOnlineInfo = new GameOnlineInfo();
    			gameOnlineInfo.setSession(session);
    			onlineAccount.getOnlineWebSocket().put(gameCode, gameOnlineInfo);
    			
    			String onlineKey = buildOnlineKey(gameCode, session);
    			onlineSessionMap.put(onlineKey, gameSession);
    		}
    		log.info("好棒哦，{}登录了游戏，gameCode={}, loginToken={}", onlineAccount.getUser().getUsername(), gameCode, loginToken);
    	}
    	catch(Exception e) {
    		log.error("建立连接失败", e);
			try {
				SysRtnCmd notifyCmd = SysRtnCmd.build(e.getMessage());
				session.getBasicRemote().sendText(JSON.toJSONString(notifyCmd));
			} catch (IOException e1) {
			}
    	}
    	
    }

	public void onClose(Session session) {
		String gameCode = session.getRequestParameterMap().get(MyConstant.GAME_CODE).get(0);
		String loginToken = session.getRequestParameterMap().get(MyConstant.LOGIN_TOKEN).get(0);
		String onlineKey = buildOnlineKey(gameCode, session);
		
		GameSession gameSession = onlineSessionMap.remove(onlineKey);
    	if(gameSession == null) {
    		log.warn("不好意思，onlineKey={}已提前失效了", onlineKey);
    		return; 
    	}
    	webGameDispatcher.onClose(gameCode, gameSession);
    	
    	OnlineAccount onlineAccount = userService.getOnlineAccount(loginToken);
    	if(onlineAccount != null) {
    		onlineAccount.getOnlineWebSocket().remove(gameCode);
    		log.info("{}用户离开了{}游戏,loginToken={}", onlineAccount.getUser().getUsername(), gameCode, loginToken);
    	}
    	else {
    		log.info("{}用户离开了{}游戏,loginToken={}", gameSession.getAttachment().get(MyConstant.PLAYER_ID), gameCode, loginToken);
    	}
	}

	public void onMessage(Session session, String message) {
		String gameCode = session.getRequestParameterMap().get(MyConstant.GAME_CODE).get(0);
		String onlineKey = buildOnlineKey(gameCode, session);
		
		GameSession gameSession = onlineSessionMap.get(onlineKey);
		if(gameSession == null) {
			log.warn("不好意思，onlineKey={}已提前失效了", onlineKey);
			try {
    			InvalidateSesssionRtnCmd rtnCmd = InvalidateSesssionRtnCmd.build();
				session.getBasicRemote().sendText(JSON.toJSONString(rtnCmd));
			} catch (IOException e) {
			}
    		return; 
		}
		
		try {
			webGameDispatcher.onMessage(gameCode, message, gameSession);
			
			String loginToken = session.getRequestParameterMap().get(MyConstant.LOGIN_TOKEN).get(0);
			OnlineAccount onlineAccount = userService.getOnlineAccount(loginToken);
	    	if(onlineAccount != null) {
	    		GameOnlineInfo old = onlineAccount.getOnlineWebSocket().get(gameCode);
	    		if(old != null) {
	    			Integer playKind = (Integer)gameSession.getAttachment().get(MyConstant.PLAY_KIND);
	    			Integer deskNo = (Integer)gameSession.getAttachment().get(MyConstant.DESK_NO);
	    			old.setPlayKind(playKind);
	    			old.setDeskNo(deskNo);
	    		}
	    	}
    	}
    	catch(Exception e) {
    		log.error("建立连接失败", e);
			try {
				SysRtnCmd notifyCmd = SysRtnCmd.build(e.getMessage());
				session.getBasicRemote().sendText(JSON.toJSONString(notifyCmd));
			} catch (IOException e1) {
			}
    	}
		
	}

	private String buildOnlineKey(String gameCode, Session session) {
		return gameCode + "--" + session.getId();
	}
}