package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushApproveApplyAssistantCmd;

import lombok.Data;

@Data
public class ReqApproveApplyAssistantCmd extends ReqSeatCmd{
	public final static int CMD = 102006;
	
	protected long otherId;
	
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushApproveApplyAssistantCmd valueOfPushApplyAssistantCmd() {
		PushApproveApplyAssistantCmd pushCmd = new PushApproveApplyAssistantCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPosition(position);
		pushCmd.setPlayerId(otherId);
		return pushCmd;
	}
}
