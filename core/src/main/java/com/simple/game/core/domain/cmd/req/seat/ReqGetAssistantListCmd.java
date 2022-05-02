package com.simple.game.core.domain.cmd.req.seat;

import lombok.Data;

@Data
public class ReqGetAssistantListCmd extends ReqSeatCmd{
	
	@Override
	public int getCode() {
		return 102003;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

}
