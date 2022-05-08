package com.simple.game.core.domain.cmd.push.game.notify;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.game.ReqApplyManagerCmd;

import lombok.Data;

@Data
public class PushNotifyApplyManagerCmd extends PushCmd{
	private long playerId;
	private String nickname;
	private String headPic;
	
	@Override
	public int getCmd() {
		return ReqApplyManagerCmd.CMD + PushCmd.NOTIFY_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
