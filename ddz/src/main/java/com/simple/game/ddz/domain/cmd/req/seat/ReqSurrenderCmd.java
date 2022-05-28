package com.simple.game.ddz.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.req.seat.ReqSeatCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushSurrenderCmd;

import lombok.Data;

@Data
public class ReqSurrenderCmd extends ReqSeatCmd{
	public final static int CMD = 151004;
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushSurrenderCmd valueOfPushSurrenderCmd() {
		PushSurrenderCmd pushCmd = new PushSurrenderCmd();
//		pushCmd.setDeskNo(deskNo);
//		pushCmd.setPlayKind(playKind);
//		pushCmd.setPosition(position);
		return pushCmd;
	}
}
