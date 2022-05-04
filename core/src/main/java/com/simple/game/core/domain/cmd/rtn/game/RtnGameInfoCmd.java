package com.simple.game.core.domain.cmd.rtn.game;

import com.simple.game.core.domain.cmd.rtn.RtnCmd;

import lombok.Data;

@Data
public class RtnGameInfoCmd extends RtnCmd{
	public final static int CODE = 101003;
	/***
	 * 暂停时长(毫秒)
	 */
	private long pauseMs;
	
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
