package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushStopOnlookerCmd;

import lombok.Data;

@Data
public class ReqStopOnlookerCmd extends ReqSeatCmd{
	
	@Override
	public int getCode() {
		return 102009;
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
