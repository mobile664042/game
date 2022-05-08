package com.simple.game.core.domain.cmd.req.seat;

import lombok.Data;

@Data
public class ReqGetSeatPlayerListCmd extends ReqSeatCmd{
	public final static int CMD = 102002;
	
	/***从0开始(每页20条)***/
	private int fromPage;
	
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
