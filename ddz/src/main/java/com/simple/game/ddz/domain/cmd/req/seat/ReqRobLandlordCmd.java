package com.simple.game.ddz.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.req.seat.ReqSeatCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushRobLandlordCmd;

import lombok.Data;

@Data
public class ReqRobLandlordCmd extends ReqSeatCmd{
	public final static int CODE = 151002;
	private int score;
	@Override
	public int getCode() {
		return CODE;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushRobLandlordCmd valueOfPushRobLandlordCmd() {
		PushRobLandlordCmd pushCmd = new PushRobLandlordCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPosition(position);
		pushCmd.setScore(score);
		return pushCmd;
	}
}
