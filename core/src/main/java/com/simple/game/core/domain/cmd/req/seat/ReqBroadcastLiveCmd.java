package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushBroadcastLiveCmd;

import lombok.Data;

@Data
public class ReqBroadcastLiveCmd extends ReqSeatCmd{
	public final static int CODE = 102016;
	@Override
	public int getCode() {
		return CODE;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushBroadcastLiveCmd valueOfPushBroadcastLiveCmd() {
		PushBroadcastLiveCmd pushCmd = new PushBroadcastLiveCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPlayerId(playerId);
		pushCmd.setPosition(position);
		return pushCmd;
	}
}
