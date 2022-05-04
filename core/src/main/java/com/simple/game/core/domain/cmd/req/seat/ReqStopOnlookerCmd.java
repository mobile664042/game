package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushStopOnlookerCmd;

import lombok.Data;

@Data
public class ReqStopOnlookerCmd extends ReqSeatCmd{
	public final static int CODE = 102009;
	@Override
	public int getCode() {
		return CODE;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushStopOnlookerCmd valueOfPushStopOnlookerCmd() {
		PushStopOnlookerCmd pushCmd = new PushStopOnlookerCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPosition(position);
		return pushCmd;
	}
}
