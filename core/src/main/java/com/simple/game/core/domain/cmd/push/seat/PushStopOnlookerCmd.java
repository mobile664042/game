package com.simple.game.core.domain.cmd.push.seat;

import lombok.Data;

@Data
public class PushStopOnlookerCmd extends PushSeatCmd{
	public final static int CODE = 1102009;
	@Override
	public int getCode() {
		return CODE;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
