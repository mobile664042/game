package com.simple.game.core.domain.cmd.req.seat;

import lombok.Data;

@Data
public class ReqGetSeatPlayerListCmd extends ReqSeatCmd{
	/***从0开始(每页20条)***/
	private int fromPage;
	
	@Override
	public int getCode() {
		return 102002;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

}
