package com.simple.game.core.domain.cmd.push.seat.notify;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApplyAssistantCmd;

import lombok.Data;

@Data
public class PushNotifyApplyAssistantCmd extends PushCmd{
	private long playerId;
	private String nickname;
	private String headPic;
	
	@Override
	public int getCmd() {
		return ReqApplyAssistantCmd.CMD + PushCmd.NOTIFY_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
