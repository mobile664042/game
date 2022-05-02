package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushStandUpCmd;

import lombok.Data;

@Data
public class ReqStandUpCmd extends ReqSeatCmd{
	
	@Override
	public int getCode() {
		return 102007;
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
