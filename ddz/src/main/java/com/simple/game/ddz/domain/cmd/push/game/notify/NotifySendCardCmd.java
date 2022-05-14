package com.simple.game.ddz.domain.cmd.push.game.notify;

import java.util.ArrayList;
import java.util.List;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.seat.PushSeatCmd;

import lombok.Data;

@Data
public class NotifySendCardCmd extends PushSeatCmd{
	private List<Integer> cards = new ArrayList<Integer>();

	@Override
	public int getCmd() {
		return 151003 + PushCmd.NOTIFY_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
