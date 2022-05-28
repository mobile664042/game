package com.simple.game.core.domain.cmd.push.game.notify;

import com.simple.game.core.domain.cmd.push.PushCmd;

import lombok.Data;

@Data
public class PushNotifyChangeSeatMasterCmd extends PushCmd{
	public final static int CMD = 2101005;
	private long position;
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
