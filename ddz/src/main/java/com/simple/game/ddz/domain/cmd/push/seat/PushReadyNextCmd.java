package com.simple.game.ddz.domain.cmd.push.seat;

import java.util.ArrayList;
import java.util.List;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.seat.PushSeatCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqReadyNextCmd;

import lombok.Data;

@Data
public class PushReadyNextCmd extends PushSeatCmd{
	private List<Integer> cards = new ArrayList<Integer>();
	

	@Override
	public int getCmd() {
		return ReqReadyNextCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
