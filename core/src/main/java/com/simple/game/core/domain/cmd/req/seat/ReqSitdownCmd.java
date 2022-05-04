package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushSitdownCmd;

import lombok.Data;

@Data
public class ReqSitdownCmd extends ReqSeatCmd{
	public final static int CODE = 102001;
	@Override
	public int getCode() {
		return CODE;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushSitdownCmd valueOfPushSitdownCmd() {
		PushSitdownCmd pushCmd = new PushSitdownCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPosition(position);
		return pushCmd;
	}
}
