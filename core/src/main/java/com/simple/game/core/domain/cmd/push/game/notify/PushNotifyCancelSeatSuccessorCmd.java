package com.simple.game.core.domain.cmd.push.game.notify;

import com.simple.game.core.domain.cmd.push.PushCmd;

import lombok.Data;

@Data
public class PushNotifyCancelSeatSuccessorCmd extends PushCmd{
	public final static int CMD = 2101004;
	private long playerId;
	private String nickname;
	private String headPic;
	
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
