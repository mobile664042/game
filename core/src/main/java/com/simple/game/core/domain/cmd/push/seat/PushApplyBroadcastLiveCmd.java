package com.simple.game.core.domain.cmd.push.seat;

import lombok.Data;

@Data
public class PushApplyBroadcastLiveCmd extends PushSeatCmd{
	private long playerId;
//	private String nickname;
	
	@Override
	public int getCode() {
		return 1102014;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
