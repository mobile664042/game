package com.simple.game.core.domain.cmd.push.seat;

import lombok.Data;

@Data
public class PushCancleBroadcastLiveCmd extends PushSeatCmd{
	private long playerId;
//	private String nickname;
	
	@Override
	public int getCode() {
		return 1102015;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
