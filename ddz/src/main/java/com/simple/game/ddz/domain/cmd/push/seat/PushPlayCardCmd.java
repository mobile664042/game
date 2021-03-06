package com.simple.game.ddz.domain.cmd.push.seat;

import java.util.ArrayList;
import java.util.List;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.seat.PushSeatCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqPlayCardCmd;

import lombok.Data;

@Data
public class PushPlayCardCmd extends PushSeatCmd{
	private List<Integer> cards = new ArrayList<Integer>();
	private int doubleFinal;
	private int residueCount;
	/***是否是强制出牌***/
	private boolean forceSend;

	@Override
	public int getCmd() {
		return ReqPlayCardCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
