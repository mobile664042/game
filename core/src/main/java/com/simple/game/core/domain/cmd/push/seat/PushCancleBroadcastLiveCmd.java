package com.simple.game.core.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqCancleBroadcastLiveCmd;

import lombok.Data;

@Data
public class PushCancleBroadcastLiveCmd extends PushSeatCmd{
	private long playerId;
	
	@Override
	public int getCode() {
		return ReqCancleBroadcastLiveCmd.CODE + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
