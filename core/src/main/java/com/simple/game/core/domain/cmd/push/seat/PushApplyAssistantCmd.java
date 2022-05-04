package com.simple.game.core.domain.cmd.push.seat;

import lombok.Data;

@Data
public class PushApplyAssistantCmd extends PushSeatCmd{
	public final static int CODE = 1102005;
	
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
