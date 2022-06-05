package com.simple.game.core.domain.cmd.rtn.game;

import com.simple.game.core.domain.cmd.req.game.ReqApplyManagerCmd;
import com.simple.game.core.domain.cmd.rtn.RtnCmd;

import lombok.Data;

@Data
public class RtnApplyManagerCmd extends RtnCmd{
	private boolean result;
	
	@Override
	public int getCmd() {
		return ReqApplyManagerCmd.CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
}
