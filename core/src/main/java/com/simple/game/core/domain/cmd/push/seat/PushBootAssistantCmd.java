package com.simple.game.core.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqBootAssistantCmd;

import lombok.Data;

@Data
public class PushBootAssistantCmd extends PushSeatCmd{
	@Override
	public int getCode() {
		return ReqBootAssistantCmd.CODE + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
