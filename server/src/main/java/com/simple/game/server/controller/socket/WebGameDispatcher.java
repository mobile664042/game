package com.simple.game.server.controller.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.simple.game.core.domain.cmd.req.ReqCmd;
import com.simple.game.core.domain.cmd.req.game.ReqConnectCmd;
import com.simple.game.core.domain.cmd.req.game.ReqDisconnectCmd;
import com.simple.game.core.domain.cmd.req.game.ReqGetOnlineListCmd;
import com.simple.game.core.domain.cmd.req.game.ReqJoinCmd;
import com.simple.game.core.domain.cmd.rtn.game.RtnGameInfoCmd;
import com.simple.game.core.exception.BizException;
import com.simple.game.core.util.GameSession;
import com.simple.game.ddz.domain.service.DdzService;
import com.simple.game.server.constant.MyConstant;
import com.simple.game.server.filter.OnlineAccount;
import com.simple.game.server.filter.OnlineAccount.GameOnlineInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebGameDispatcher {
	
	
	
	@Autowired
	private DdzService ddzService;


	/***
	 * 游戲轉發處理
	 * @param gameCode
	 * @param message
	 * @param onlineAccount
	 */
	public void onMessage(String gameCode, String message, OnlineAccount onlineAccount) {
		if(MyConstant.DDZ.equals(gameCode)) {
			ReqCmd reqCmd = parseAndCheck(message);
			if(reqCmd instanceof ReqJoinCmd) {
				doDdzJoin((ReqJoinCmd)reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqGetOnlineListCmd) {
				ddzService.getOnlineList((ReqGetOnlineListCmd)reqCmd);
				return ;
			}
    	}		
	}
	
	private static ReqCmd parseReqCmd(String message) {
		JSONObject jsonObject = JSON.parseObject(message);
		
		int code = jsonObject.getIntValue("code");
		switch(code) {
		case 101003:
			return JSON.parseObject(message, ReqJoinCmd.class);
			
		case 101004:
			return JSON.parseObject(message, ReqGetOnlineListCmd.class);
			
			//TODO 
		
		}
		throw new BizException("无效的参数3：" + message);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void doDdzJoin(ReqJoinCmd reqJoinCmd, OnlineAccount onlineAccount) {
		//传递系统参数
		reqJoinCmd.setPlayerId(onlineAccount.getUser().getId());
		reqJoinCmd.setNickname(onlineAccount.getUser().getNickname());
		reqJoinCmd.setSex(onlineAccount.getUser().getSex());
		reqJoinCmd.setTelphone(onlineAccount.getUser().getTelphone());
		reqJoinCmd.setHeadPic(onlineAccount.getUser().getHeadPic());
		//TODO 
		reqJoinCmd.setBcoin(100000);
		GameOnlineInfo gameOnlineInfo = onlineAccount.getOnlineWebSocket().get(MyConstant.DDZ);
		GameSession gameSession = new MyGameSession(gameOnlineInfo.getSession());
		reqJoinCmd.setSession(gameSession);
		
		RtnGameInfoCmd rtnGameInfoCmd = ddzService.join(reqJoinCmd);
		gameOnlineInfo.setPlayKind(reqJoinCmd.getPlayKind());
		gameOnlineInfo.setDeskNo(reqJoinCmd.getDeskNo());
		
		//把结果写回给用户
		gameSession.write(rtnGameInfoCmd);
	}
	
	private static ReqCmd parseAndCheck(String message) {
		ReqCmd reqCmd = null;
		try {
			reqCmd = parseReqCmd(message);
		}
		catch(Exception e) {
			log.warn("无效的参数1：" + message);
			throw new BizException("无效的参数1：" + message, e);
		}
		
		if(reqCmd == null) {
			log.warn("无效的参数2：" + message);
			throw new BizException("无效的参数2：" + message);
		}
		
		reqCmd.checkParam();
		return reqCmd;
    }

	public void onClose(String gameCode, OnlineAccount onlineAccount) {
		if(MyConstant.DDZ.equals(gameCode)) {
			GameOnlineInfo gameOnlineInfo = onlineAccount.getOnlineWebSocket().get(MyConstant.DDZ);
			ReqDisconnectCmd reqCmd = new ReqDisconnectCmd();
			reqCmd.setPlayKind(gameOnlineInfo.getPlayKind());
			reqCmd.setDeskNo(gameOnlineInfo.getDeskNo());
			reqCmd.setPlayerId(onlineAccount.getUser().getId());
			
			//TODO 
			ddzService.disconnect(reqCmd);
    	}			
	}

	public void onReOpen(String gameCode, OnlineAccount onlineAccount) {
		if(MyConstant.DDZ.equals(gameCode)) {
			GameOnlineInfo gameOnlineInfo = onlineAccount.getOnlineWebSocket().get(MyConstant.DDZ);
			GameSession gameSession = new MyGameSession(gameOnlineInfo.getSession());
			
			ReqConnectCmd reqCmd = new ReqConnectCmd();
			reqCmd.setPlayKind(gameOnlineInfo.getPlayKind());
			reqCmd.setDeskNo(gameOnlineInfo.getDeskNo());
			reqCmd.setPlayerId(onlineAccount.getUser().getId());
			reqCmd.setSession(gameSession);
			
			ddzService.connected(reqCmd);
    	}				
	}
	
	
	
	
}