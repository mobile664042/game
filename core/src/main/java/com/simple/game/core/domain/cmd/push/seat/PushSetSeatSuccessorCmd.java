package com.simple.game.core.domain.cmd.push.seat;

import lombok.Data;

@Data
public class PushSetSeatSuccessorCmd extends PushSeatCmd{
	private long playerId;
	@Override
	public int getCode() {
		return 1102012;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
