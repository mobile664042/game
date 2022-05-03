package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.Cmd;
import com.simple.game.core.domain.cmd.push.game.PushResumeCmd;

import lombok.Data;

@Data
public class ReqAdminResumeCmd extends Cmd{
	protected int playKind;
	protected int deskNo;
	
	@Override
	public int getCode() {
		return 201002;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushResumeCmd valueOfPushResumeCmd() {
		PushResumeCmd pushCmd = new PushResumeCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		return pushCmd;
	}
}
