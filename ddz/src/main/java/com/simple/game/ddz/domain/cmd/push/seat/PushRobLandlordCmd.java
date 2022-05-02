package com.simple.game.ddz.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.seat.PushSeatCmd;

import lombok.Data;

@Data
public class PushRobLandlordCmd extends PushSeatCmd{
	private int score;

	@Override
	public int getCode() {
		return 1151002;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
