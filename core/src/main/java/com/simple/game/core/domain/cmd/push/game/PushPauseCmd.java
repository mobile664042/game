package com.simple.game.core.domain.cmd.push.game;

import lombok.Data;

@Data
public class PushPauseCmd extends PushGameCmd{
	private int seconds;
	
	@Override
	public int getCode() {
		return 1101001;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
