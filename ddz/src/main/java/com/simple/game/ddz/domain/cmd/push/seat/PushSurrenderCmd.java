package com.simple.game.ddz.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.seat.PushSeatCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqSurrenderCmd;

import lombok.Data;

@Data
public class PushSurrenderCmd extends PushSeatCmd{
	@Override
	public int getCode() {
		return ReqSurrenderCmd.CODE + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
