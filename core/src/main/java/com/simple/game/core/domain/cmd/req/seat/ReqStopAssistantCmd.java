package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushStopAssistantCmd;

import lombok.Data;

@Data
public class ReqStopAssistantCmd extends ReqSeatCmd{
	public final static int CMD = 102008;
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushStopAssistantCmd valueOfPushStopAssistantCmd() {
		PushStopAssistantCmd pushCmd = new PushStopAssistantCmd();
//		pushCmd.setDeskNo(deskNo);
//		pushCmd.setPlayKind(playKind);
//		pushCmd.setPosition(position);
		return pushCmd;
	}
}
