package com.simple.game.core.domain.cmd.push.seat.notify;

import com.simple.game.core.domain.cmd.push.PushCmd;

public class PushNotifyApplyAssistantCmd extends PushCmd{
	public final static int CODE = 2102005;
	
	@Override
	public int getCode() {
		return CODE;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
