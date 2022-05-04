package com.simple.game.core.domain.cmd.push.seat.notify;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApplyAssistantCmd;

public class PushNotifyApplyAssistantCmd extends PushCmd{
	@Override
	public int getCode() {
		return ReqApplyAssistantCmd.CODE + PushCmd.NOTIFY_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
