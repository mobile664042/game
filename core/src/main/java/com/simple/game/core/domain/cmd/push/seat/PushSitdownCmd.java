package com.simple.game.core.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqSitdownCmd;
import com.simple.game.core.domain.cmd.vo.DdzSeatPlayerVo;

import lombok.Data;

@Data
public class PushSitdownCmd extends PushSeatCmd{
	private DdzSeatPlayerVo player;
	
	@Override
	public int getCmd() {
		return ReqSitdownCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
