package com.simple.game.core.domain.cmd.push.seat;

import lombok.Data;

@Data
public class PushStandUpCmd extends PushSeatCmd{
	public final static int CODE = 1102007;
	private long playerId;
	private String nickname;
	private String headPic;
	
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
