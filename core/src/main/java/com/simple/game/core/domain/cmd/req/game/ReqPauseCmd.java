package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.push.game.PushPauseCmd;

import lombok.Data;

@Data
public class ReqPauseCmd extends ReqGameCmd{
	public final static int CMD = 101001;
	private int seconds;
	
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushPauseCmd valueOfPushPauseCmd() {
		PushPauseCmd pushCmd = new PushPauseCmd();
//		pushCmd.setDeskNo(deskNo);
//		pushCmd.setPlayKind(playKind);
		pushCmd.setSeconds(seconds);
		return pushCmd;
	}
}
