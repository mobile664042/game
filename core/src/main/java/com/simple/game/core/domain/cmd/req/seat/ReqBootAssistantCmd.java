package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushBootAssistantCmd;

import lombok.Data;

@Data
public class ReqBootAssistantCmd extends ReqSeatCmd{
	public final static int CMD = 102010;
	
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushBootAssistantCmd valueOfPushBootAssistantCmd() {
		PushBootAssistantCmd pushCmd = new PushBootAssistantCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPosition(position);
		return pushCmd;
	}
}
