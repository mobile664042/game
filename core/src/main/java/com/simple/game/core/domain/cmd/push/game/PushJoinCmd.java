package com.simple.game.core.domain.cmd.push.game;

import com.simple.game.core.domain.cmd.vo.PlayerVo;

import lombok.Data;

@Data
public class PushJoinCmd extends PushGameCmd{
	private PlayerVo player;
	
	@Override
	public int getCode() {
		return 1101003;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
