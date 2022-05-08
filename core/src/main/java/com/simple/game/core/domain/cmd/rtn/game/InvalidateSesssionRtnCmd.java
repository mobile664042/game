package com.simple.game.core.domain.cmd.rtn.game;

import com.simple.game.core.domain.cmd.rtn.RtnCmd;

import lombok.Data;

@Data
public class InvalidateSesssionRtnCmd extends RtnCmd{
	public final static int CMD = 101000;
	
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public static InvalidateSesssionRtnCmd build() {
		InvalidateSesssionRtnCmd cmd = new InvalidateSesssionRtnCmd();
		cmd.setCode(500);
		cmd.setMessage("你的外部session以经掉线了，请重新登录!");
		return cmd;
	}

}
