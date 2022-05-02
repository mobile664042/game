package com.simple.game.core.domain.cmd.push.seat;

import lombok.Data;

@Data
public class PushStopOnlookerCmd extends PushSeatCmd{
	@Override
	public int getCode() {
		return 1102009;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
