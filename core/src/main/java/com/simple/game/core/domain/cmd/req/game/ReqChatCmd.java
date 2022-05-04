package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.push.game.PushChatCmd;
import com.simple.game.core.domain.ext.Chat;

import lombok.Data;

@Data
public class ReqChatCmd extends ReqGameCmd{
	public final static int CODE = 101006;
	
	private Chat chat;
	@Override
	public int getCode() {
		return CODE;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushChatCmd valueOfPushChatCmd() {
		PushChatCmd pushCmd = new PushChatCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPlayerId(playerId);
		pushCmd.setChat(chat);
		return pushCmd;
	}
}
