package com.simple.game.core.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqSetSeatSuccessorCmd;

import lombok.Data;

@Data
public class PushSetSeatSuccessorCmd extends PushSeatCmd{
	private long playerId;
	private String nickname;
	private String headPic;
	@Override
	public int getCmd() {
		return ReqSetSeatSuccessorCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
