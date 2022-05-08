package com.simple.game.core.domain.cmd.req.game;

import lombok.Data;

@Data
public class ReqGetOnlineListCmd extends ReqGameCmd{
	public final static int CMD = 101004;
	
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
