package com.simple.game.core.domain.cmd.push.game;

import lombok.Data;

@Data
public class PushKickoutCmd extends PushGameCmd{
	public final static int CODE = 1101013;
	
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
