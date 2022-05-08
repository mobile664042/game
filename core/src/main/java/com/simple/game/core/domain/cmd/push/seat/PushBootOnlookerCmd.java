package com.simple.game.core.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqBootOnlookerCmd;

import lombok.Data;

@Data
public class PushBootOnlookerCmd extends PushSeatCmd{
	@Override
	public int getCmd() {
		return ReqBootOnlookerCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
