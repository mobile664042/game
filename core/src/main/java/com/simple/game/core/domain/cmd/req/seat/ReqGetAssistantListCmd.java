package com.simple.game.core.domain.cmd.req.seat;

import lombok.Data;

@Data
public class ReqGetAssistantListCmd extends ReqSeatCmd{
	public final static int CMD = 102003;
	
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
