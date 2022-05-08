package com.simple.game.core.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStopAssistantCmd;

import lombok.Data;

@Data
public class PushStopAssistantCmd extends PushSeatCmd{
	@Override
	public int getCmd() {
		return ReqStopAssistantCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
