package com.simple.game.core.domain.cmd.push.game;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.game.ReqResumeCmd;

import lombok.Data;

@Data
public class PushResumeCmd extends PushGameCmd{
	
	@Override
	public int getCmd() {
		return ReqResumeCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
