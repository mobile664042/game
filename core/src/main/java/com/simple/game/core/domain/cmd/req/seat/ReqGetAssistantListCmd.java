package com.simple.game.core.domain.cmd.req.seat;

import lombok.Data;

@Data
public class ReqGetAssistantListCmd extends ReqSeatCmd{
	public final static int CODE = 102003;
	
	@Override
	public int getCode() {
		return CODE;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

}
