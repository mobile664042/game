package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.Cmd;
import com.simple.game.core.domain.cmd.push.game.PushResumeCmd;

import lombok.Data;

@Data
public class ReqAdminResumeCmd extends Cmd{
	public final static int CODE = 201002;
	
	protected int playKind;
	protected int deskNo;
	
	@Override
	public int getCode() {
		return CODE;
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
