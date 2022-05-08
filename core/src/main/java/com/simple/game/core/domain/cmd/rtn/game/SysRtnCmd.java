package com.simple.game.core.domain.cmd.rtn.game;

import com.simple.game.core.domain.cmd.rtn.RtnCmd;

import lombok.Data;

@Data
public class SysRtnCmd extends RtnCmd{
	public final static int CMD = 102000;
	
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public static SysRtnCmd build(String message) {
		SysRtnCmd cmd = new SysRtnCmd();
		cmd.setCode(500);
		cmd.setMessage(message);
		return cmd;
	}

}
