package com.simple.game.core.domain.cmd.push.game;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.game.ReqJoinCmd;
import com.simple.game.core.domain.cmd.vo.PlayerVo;

import lombok.Data;

@Data
public class PushJoinCmd extends PushGameCmd{
	private PlayerVo player;
	
	@Override
	public int getCmd() {
		return ReqJoinCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
