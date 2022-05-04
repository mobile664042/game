package com.simple.game.core.domain.cmd.push.game;

import lombok.Data;

@Data
public class PushPauseCmd extends PushGameCmd{
	public final static int CODE = 1101001;
	
	private int seconds;
	
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
