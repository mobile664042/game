package com.simple.game.ddz.domain.cmd.push.seat;

import java.util.List;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.seat.PushSeatCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqDoubledShowCardCmd;

import lombok.Data;

@Data
public class PushDoubleShowCardCmd extends PushSeatCmd{
	/***总共翻倍几次***/
	protected Integer doubleFinal;
	
	protected List<Integer> cards;

	@Override
	public int getCmd() {
		return ReqDoubledShowCardCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
