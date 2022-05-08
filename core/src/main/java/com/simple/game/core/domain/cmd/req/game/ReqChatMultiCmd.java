package com.simple.game.core.domain.cmd.req.game;

import java.util.List;

import com.simple.game.core.domain.cmd.push.game.PushChatMultiCmd;
import com.simple.game.core.domain.ext.Chat;

import lombok.Data;

@Data
public class ReqChatMultiCmd extends ReqGameCmd{
	public final static int CMD = 101010;
	
	private List<Integer> positionList;
	private Chat chat;
	@Override
	public int getCmd() {
		return 101010;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushChatMultiCmd valueOfPushChatMultiCmd() {
		PushChatMultiCmd pushCmd = new PushChatMultiCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPlayerId(playerId);
		pushCmd.setPositionList(positionList);
		pushCmd.setChat(chat);
		return pushCmd;
	}
}
