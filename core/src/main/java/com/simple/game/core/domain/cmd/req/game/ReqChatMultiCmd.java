package com.simple.game.core.domain.cmd.req.game;

import java.util.List;

import com.simple.game.core.domain.cmd.push.game.PushChatMultiCmd;
import com.simple.game.core.domain.ext.Chat;

import lombok.Data;

@Data
public class ReqChatMultiCmd extends ReqGameCmd{
	private List<Integer> positionList;
	private Chat chat;
	@Override
	public int getCode() {
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
