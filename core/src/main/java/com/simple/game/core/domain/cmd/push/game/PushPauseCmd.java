package com.simple.game.core.domain.cmd.push.game;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.game.ReqPauseCmd;

import lombok.Data;

@Data
public class PushPauseCmd extends PushGameCmd{
	private int seconds;
	
	@Override
	public int getCmd() {
		return ReqPauseCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
