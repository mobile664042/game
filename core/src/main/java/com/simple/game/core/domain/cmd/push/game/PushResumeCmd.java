package com.simple.game.core.domain.cmd.push.game;

import lombok.Data;

@Data
public class PushResumeCmd extends PushGameCmd{
	
	@Override
	public int getCode() {
		return 1101002;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
