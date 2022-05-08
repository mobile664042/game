package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushBootOnlookerCmd;

import lombok.Data;

@Data
public class ReqBootOnlookerCmd extends ReqSeatCmd{
	public final static int CMD = 102011;
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushBootOnlookerCmd valueOfPushBootOnlookerCmd() {
		PushBootOnlookerCmd pushCmd = new PushBootOnlookerCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPosition(position);
		return pushCmd;
	}
}
