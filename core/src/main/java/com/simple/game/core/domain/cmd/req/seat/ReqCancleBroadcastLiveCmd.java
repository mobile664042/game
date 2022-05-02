package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushCancleBroadcastLiveCmd;

import lombok.Data;

@Data
public class ReqCancleBroadcastLiveCmd extends ReqSeatCmd{
	@Override
	public int getCode() {
		return 102015;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushCancleBroadcastLiveCmd valueOfPushCancleBroadcastLiveCmd() {
		PushCancleBroadcastLiveCmd pushCmd = new PushCancleBroadcastLiveCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPlayerId(playerId);
		pushCmd.setPosition(position);
		return pushCmd;
	}
}
