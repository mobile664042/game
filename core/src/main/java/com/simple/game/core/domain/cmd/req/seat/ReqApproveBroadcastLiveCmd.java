package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushApproveBroadcastLiveCmd;

import lombok.Data;

@Data
public class ReqApproveBroadcastLiveCmd extends ReqSeatCmd{
	public final static int CODE = 102017;
	
	@Override
	public int getCode() {
		return CODE;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushApproveBroadcastLiveCmd valueOfPushApproveBroadcastLiveCmd() {
		PushApproveBroadcastLiveCmd pushCmd = new PushApproveBroadcastLiveCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPosition(position);
		return pushCmd;
	}
}
