package com.simple.game.ddz.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.seat.PushSeatCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqDoubledCmd;

import lombok.Data;

@Data
public class PushDoubledCmd extends PushSeatCmd{
	/***总共翻倍几次***/
	protected int doubleFinal;

	@Override
	public int getCmd() {
		return ReqDoubledCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
