package com.simple.game.ddz.domain.cmd.rtn.seat;

import com.simple.game.core.domain.cmd.rtn.RtnCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqPlayCardCmd;

import lombok.Data;

@Data
public class RtnPlayCardCmd extends RtnCmd{
	/***倍***/
	private int doubleFinal;
	/***剩余的牌数***/
	private int residueCount;
	
	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCmd() {
		return ReqPlayCardCmd.CMD;
	}
}
