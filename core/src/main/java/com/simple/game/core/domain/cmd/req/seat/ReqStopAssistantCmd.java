package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushStopAssistantCmd;

import lombok.Data;

@Data
public class ReqStopAssistantCmd extends ReqSeatCmd{
	public final static int CODE = 102008;
	@Override
	public int getCode() {
		return CODE;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushStopAssistantCmd valueOfPushStopAssistantCmd() {
		PushStopAssistantCmd pushCmd = new PushStopAssistantCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPosition(position);
		return pushCmd;
	}
}
