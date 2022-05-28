package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.push.game.PushResumeCmd;

import lombok.Data;

@Data
public class ReqResumeCmd extends ReqGameCmd{
	public final static int CMD = 101002;
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushResumeCmd valueOfPushResumeCmd() {
		PushResumeCmd pushCmd = new PushResumeCmd();
//		pushCmd.setDeskNo(deskNo);
//		pushCmd.setPlayKind(playKind);
		return pushCmd;
	}
}
