package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushApplyBroadcastLiveCmd;

import lombok.Data;

@Data
public class ReqApplyBroadcastLiveCmd extends ReqSeatCmd{
	@Override
	public int getCode() {
		return 102014;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushApplyBroadcastLiveCmd valueOfPushApplyBroadcastLiveCmd() {
		PushApplyBroadcastLiveCmd pushCmd = new PushApplyBroadcastLiveCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPlayerId(playerId);
		pushCmd.setPosition(position);
		return pushCmd;
	}
}
