package com.simple.game.core.domain.cmd.push.seat;

import lombok.Data;

@Data
public class PushCancleBroadcastLiveCmd extends PushSeatCmd{
	public final static int CODE = 1102015;
	private long playerId;
	private String nickname;
	
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
