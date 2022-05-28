package com.simple.game.core.domain.dto.desk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.push.game.PushChatCmd;
import com.simple.game.core.domain.cmd.push.game.PushChatMultiCmd;
import com.simple.game.core.domain.cmd.push.game.notify.PushSysChatCmd;
import com.simple.game.core.domain.cmd.req.game.ReqChatCmd;
import com.simple.game.core.domain.cmd.req.game.ReqChatMultiCmd;
import com.simple.game.core.domain.dto.DeskPlugin;
import com.simple.game.core.domain.dto.GameSessionInfo;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.TableDesk;
import com.simple.game.core.domain.ext.Chat;
import com.simple.game.core.exception.BizException;

public class ChatPlugin extends DeskPlugin{
	private final static Logger logger = LoggerFactory.getLogger(ChatPlugin.class);
	
	public static final String PLUGIN_NAME = "chat";
	
	
	public ChatPlugin(TableDesk tableDesk) {
		super(tableDesk, null);
	}

	/***聊天****/
	public void chat(GameSessionInfo gameSessionInfo, ReqChatCmd reqCmd) {
		Player player = tableDesk.getPlayer(gameSessionInfo.getPlayerId());
		
		//广播聊天信息
		PushChatCmd pushCmd = reqCmd.valueOfPushChatCmd();
		pushCmd.setPlayerId(gameSessionInfo.getPlayerId());
		pushCmd.setNickname(player.getNickname());
		pushCmd.setHeadPic(player.getHeadPic());
		tableDesk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
		logger.info("{}在游戏:{},发送:{}聊天", player.getNickname(), tableDesk.getGameItem().getName(), reqCmd.getChat().getKind());
	}
	
	/***聊天****/
	public void chat(GameSessionInfo gameSessionInfo, ReqChatMultiCmd reqCmd) {
		if(reqCmd.getPositionList() == null || reqCmd.getPositionList().size() == 0) {
			throw new BizException("席位不能为空");
		}
		
		Player player = tableDesk.getPlayer(gameSessionInfo.getPlayerId());
		
		PushChatMultiCmd pushCmd = reqCmd.valueOfPushChatMultiCmd();
		pushCmd.setPlayerId(gameSessionInfo.getPlayerId());
		pushCmd.setNickname(player.getNickname());
		pushCmd.setHeadPic(player.getHeadPic());
		//发送广播
		tableDesk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
		logger.info("{}在游戏:{},发送:{}聊天", player.getNickname(), tableDesk.getGameItem().getName(), reqCmd.getChat().getKind());
	}

	/***聊天****/
	public final void chat(long playerId, Chat message) {
		Player player = tableDesk.getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("非法请求，不在游戏桌中"));
		}
		PushSysChatCmd pushCmd = new PushSysChatCmd();
		pushCmd.setChat(message);
		player.getOnline().push(pushCmd);
		
		logger.info("{}在游戏:{},发送:{}聊天", player.getNickname(), tableDesk.getGameItem().getName(), message.getKind());
	}
	
	

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
}
