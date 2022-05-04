package com.simple.game.ddz.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.seat.PushSeatCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqRobLandlordCmd;

import lombok.Data;

@Data
public class PushRobLandlordCmd extends PushSeatCmd{
	private int score;

	@Override
	public int getCode() {
		return ReqRobLandlordCmd.CODE + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
