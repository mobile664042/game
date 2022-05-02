package com.simple.game.core.domain.cmd.push.seat;

import lombok.Data;

@Data
public class PushApplyAssistantCmd extends PushSeatCmd{
	private long playerId;
	private String nickname;
	
	@Override
	public int getCode() {
		return 1102005;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
