package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.push.game.PushJoinCmd;

import lombok.Data;

@Data
public class ReqGetOnlineListCmd extends ReqGameCmd{
	/***从0开始(每页20条)***/
	private int fromPage;
	
	@Override
	public int getCode() {
		return 101004;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushJoinCmd valueOfPushJoinCmd() {
		PushJoinCmd pushCmd = new PushJoinCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		return pushCmd;
	}
}
