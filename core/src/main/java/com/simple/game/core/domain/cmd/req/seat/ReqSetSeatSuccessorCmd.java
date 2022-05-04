package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushSetSeatSuccessorCmd;

import lombok.Data;

@Data
public class ReqSetSeatSuccessorCmd extends ReqSeatCmd{
	public final static int CODE = 102012;
	private long otherId;
	@Override
	public int getCode() {
		return CODE;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushSetSeatSuccessorCmd valueOfPushSetSeatSuccessorCmd() {
		PushSetSeatSuccessorCmd pushCmd = new PushSetSeatSuccessorCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPosition(position);
		pushCmd.setPlayerId(otherId);
		return pushCmd;
	}
}
