package com.simple.game.core.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStopOnlookerCmd;

import lombok.Data;

@Data
public class PushStopOnlookerCmd extends PushSeatCmd{
	@Override
	public int getCmd() {
		return ReqStopOnlookerCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
