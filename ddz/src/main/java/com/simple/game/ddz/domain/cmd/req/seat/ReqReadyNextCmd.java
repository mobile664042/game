package com.simple.game.ddz.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.req.seat.ReqSeatCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushReadyNextCmd;

import lombok.Data;

@Data
public class ReqReadyNextCmd extends ReqSeatCmd{
	
	@Override
	public int getCode() {
		return 151001;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushReadyNextCmd valueOfPushReadyNextCmd() {
		PushReadyNextCmd pushCmd = new PushReadyNextCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPosition(position);
		return pushCmd;
	}
}
