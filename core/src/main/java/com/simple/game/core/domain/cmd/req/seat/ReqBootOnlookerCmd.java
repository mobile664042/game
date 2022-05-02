package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.push.seat.PushBootOnlookerCmd;

import lombok.Data;

@Data
public class ReqBootOnlookerCmd extends ReqSeatCmd{
	
	@Override
	public int getCode() {
		return 102011;
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
