package com.simple.game.core.domain.cmd.push.seat;

import lombok.Data;

@Data
public class PushBootOnlookerCmd extends PushSeatCmd{
	@Override
	public int getCode() {
		return 1102011;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
