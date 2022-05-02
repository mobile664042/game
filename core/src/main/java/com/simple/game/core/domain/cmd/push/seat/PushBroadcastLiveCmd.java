package com.simple.game.core.domain.cmd.push.seat;

import lombok.Data;

@Data
public class PushBroadcastLiveCmd extends PushSeatCmd{
	private long playerId;
//	private String nickname;
	
	@Override
	public int getCode() {
		return 1102016;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
