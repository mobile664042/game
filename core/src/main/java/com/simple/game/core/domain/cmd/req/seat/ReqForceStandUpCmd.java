package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushStandUpCmd;

import lombok.Data;

@Data
public class ReqForceStandUpCmd extends ReqSeatCmd{
	public final static int CMD = 102013;
	
	private long otherId;
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushStandUpCmd valueOfPushStandUpCmd() {
		PushStandUpCmd pushCmd = new PushStandUpCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPosition(position);
		pushCmd.setPlayerId(otherId);
		return pushCmd;
	}
}
