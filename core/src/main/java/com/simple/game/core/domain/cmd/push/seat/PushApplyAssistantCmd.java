package com.simple.game.core.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApplyAssistantCmd;

import lombok.Data;

@Data
public class PushApplyAssistantCmd extends PushSeatCmd{
	private long playerId;
	private String nickname;
	private String headPic;
	
	@Override
	public int getCmd() {
		return ReqApplyAssistantCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
