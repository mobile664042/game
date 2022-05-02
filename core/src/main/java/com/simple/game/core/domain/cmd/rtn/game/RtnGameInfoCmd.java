package com.simple.game.core.domain.cmd.rtn.game;

import com.simple.game.core.domain.cmd.rtn.RtnCmd;

import lombok.Data;

@Data
public class RtnGameInfoCmd extends RtnCmd{
	/***
	 * 暂停时长(毫秒)
	 */
	private long pauseMs;
	
	@Override
	public int getCode() {
		return 101003;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
