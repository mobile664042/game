package com.simple.game.core.domain.cmd.push.game;

import com.simple.game.core.domain.cmd.vo.PlayerVo;

import lombok.Data;

@Data
public class PushJoinCmd extends PushGameCmd{
	public final static int CODE = 1101003;
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
