package com.simple.game.core.domain.cmd.push.game;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.game.ReqKickoutCmd;

import lombok.Data;

@Data
public class PushKickoutCmd extends PushGameCmd{
	
	private long playerId;
	private String nickname;
	private String headPic;
	
	@Override
	public int getCmd() {
		return ReqKickoutCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
