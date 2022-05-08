package com.simple.game.core.domain.cmd.req.seat;

import lombok.Data;

@Data
public class ReqApplySeatSuccessorCmd extends ReqSeatCmd{
	public final static int CMD = 102018;
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
