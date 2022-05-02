package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushBroadcastLiveCmd;

import lombok.Data;

@Data
public class ReqBroadcastLiveLiveCmd extends ReqSeatCmd{
	@Override
	public int getCode() {
		return 102016;
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
