package com.simple.game.ddz.domain.cmd.req.seat;

import java.util.List;

import com.simple.game.core.domain.cmd.req.seat.ReqSeatCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushPlayCardCmd;

import lombok.Data;

@Data
public class ReqPlayCardCmd extends ReqSeatCmd{
	private List<Integer> cards;
	@Override
	public int getCode() {
		return 151003;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushPlayCardCmd valueOfPushPlayCardCmd() {
		PushPlayCardCmd pushCmd = new PushPlayCardCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		pushCmd.setPosition(position);
		pushCmd.setCards(cards);
		return pushCmd;
	}
}
