package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushStandUpCmd;

import lombok.Data;

@Data
public class ReqStandUpCmd extends ReqSeatCmd{
	public final static int CODE = 102007;
	@Override
	public int getCode() {
		return CODE;
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
		pushCmd.setPlayerId(playerId);
		return pushCmd;
	}
}
