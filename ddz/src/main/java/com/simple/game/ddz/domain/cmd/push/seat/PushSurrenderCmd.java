package com.simple.game.ddz.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.seat.PushSeatCmd;

import lombok.Data;

@Data
public class PushSurrenderCmd extends PushSeatCmd{
	public final static int CODE = 1151003;

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
