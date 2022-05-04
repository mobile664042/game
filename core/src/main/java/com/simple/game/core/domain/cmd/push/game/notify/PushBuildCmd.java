package com.simple.game.core.domain.cmd.push.game.notify;

import com.simple.game.core.domain.cmd.push.PushCmd;

import lombok.Data;

@Data
public class PushBuildCmd extends PushCmd{
	public final static int CODE = 2101002;
	protected int playKind;
	
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
