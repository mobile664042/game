package com.simple.game.ddz.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.req.seat.ReqSeatCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushDoubledCmd;

import lombok.Data;

@Data
public class ReqDoubledCmd extends ReqSeatCmd{
	public final static int CMD = 151005;
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushDoubledCmd valueOfPushDoubledCmd() {
		PushDoubledCmd pushCmd = new PushDoubledCmd();
//		pushCmd.setDeskNo(deskNo);
//		pushCmd.setPlayKind(playKind);
//		pushCmd.setPosition(position);
//		pushCmd.setScore(score);
		return pushCmd;
	}
}
