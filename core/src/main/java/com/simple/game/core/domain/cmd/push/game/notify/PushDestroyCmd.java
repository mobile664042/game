package com.simple.game.core.domain.cmd.push.game.notify;

import com.simple.game.core.domain.cmd.push.game.PushGameCmd;

import lombok.Data;

@Data
public class PushDestroyCmd extends PushGameCmd{
	public final static int CODE = 2101001;
	
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
