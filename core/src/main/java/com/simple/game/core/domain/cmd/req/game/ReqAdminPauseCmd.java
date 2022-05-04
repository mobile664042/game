package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.Cmd;
import com.simple.game.core.domain.cmd.push.game.PushPauseCmd;

import lombok.Data;

@Data
public class ReqAdminPauseCmd extends Cmd{
	public final static int CODE = 201001;
	
	protected int playKind;
	protected int deskNo;
	private int seconds;
	
	@Override
	public int getCode() {
		return CODE;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushPauseCmd valueOfPushPauseCmd() {
		PushPauseCmd pushCmd = new PushPauseCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setSeconds(seconds);
		return pushCmd;
	}
}
