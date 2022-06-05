package com.simple.game.core.domain.cmd.req.game;

import lombok.Data;

@Data
public class ReqKickoutCmd extends ReqGameCmd{
	public final static int CMD = 101013;
	
	private Long otherId;
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
