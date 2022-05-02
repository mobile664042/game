package com.simple.game.core.domain.cmd.push.game.notify;

import com.simple.game.core.domain.cmd.push.PushCmd;

import lombok.Data;

@Data
public class PushBuildCmd extends PushCmd{
	protected int playKind;
	
	@Override
	public int getCode() {
		return 2101002;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
