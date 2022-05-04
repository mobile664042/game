package com.simple.game.core.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.vo.PlayerVo;

import lombok.Data;

@Data
public class PushSitdownCmd extends PushSeatCmd{
	public final static int CODE = 1102001;
	private PlayerVo player;
	
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
