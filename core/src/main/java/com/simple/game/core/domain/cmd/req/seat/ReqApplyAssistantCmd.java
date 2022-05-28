package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushApplyAssistantCmd;

import lombok.Data;

@Data
public class ReqApplyAssistantCmd extends ReqSeatCmd{
	public final static int CMD = 102005;
	
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushApplyAssistantCmd valueOfPushApplyAssistantCmd() {
		PushApplyAssistantCmd pushCmd = new PushApplyAssistantCmd();
//		pushCmd.setDeskNo(deskNo);
//		pushCmd.setPlayKind(playKind);
//		pushCmd.setPosition(position);
//		pushCmd.setPlayerId(playerId);
		return pushCmd;
	}
}
