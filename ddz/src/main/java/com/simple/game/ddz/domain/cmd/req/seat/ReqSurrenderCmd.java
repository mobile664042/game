package com.simple.game.ddz.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.req.seat.ReqSeatCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushSurrenderCmd;

import lombok.Data;

@Data
public class ReqSurrenderCmd extends ReqSeatCmd{
	@Override
	public int getCode() {
		return 151004;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushSurrenderCmd valueOfPushSurrenderCmd() {
		PushSurrenderCmd pushCmd = new PushSurrenderCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPosition(position);
		return pushCmd;
	}
}
