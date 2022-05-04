package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.push.game.PushJoinCmd;

import lombok.Data;

@Data
public class ReqGetOnlineListCmd extends ReqGameCmd{
	public final static int CODE = 101004;
	
	/***从0开始(每页20条)***/
	private int fromPage;
	
	@Override
	public int getCode() {
		return CODE;
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
