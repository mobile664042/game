package com.simple.game.core.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStopOnlookerCmd;

import lombok.Data;

@Data
public class PushStopOnlookerCmd extends PushSeatCmd{
	@Override
	public int getCode() {
		return ReqStopOnlookerCmd.CODE + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
