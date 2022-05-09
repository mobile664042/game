package com.simple.game.core.domain.cmd.req;

public class HeartCmd extends ReqCmd{
	public final static int CMD = 888888;
	
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
}
