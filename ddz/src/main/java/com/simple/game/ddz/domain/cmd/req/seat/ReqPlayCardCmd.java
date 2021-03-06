package com.simple.game.ddz.domain.cmd.req.seat;

import java.util.ArrayList;
import java.util.List;

import com.simple.game.core.domain.cmd.req.seat.ReqSeatCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushPlayCardCmd;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ReqPlayCardCmd extends ReqSeatCmd{
	public final static int CMD = 151003;
	private List<Integer> cards;
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushPlayCardCmd valueOfPushPlayCardCmd() {
		PushPlayCardCmd pushCmd = new PushPlayCardCmd();
//		pushCmd.setDeskNo(deskNo);
//		pushCmd.setPlayKind(playKind);
//		pushCmd.setPosition(position);
		if(cards != null) {
			pushCmd.setCards(new ArrayList<Integer>(cards));
		}
		else {
			pushCmd.setCards(cards);
		}
		return pushCmd;
	}
}
