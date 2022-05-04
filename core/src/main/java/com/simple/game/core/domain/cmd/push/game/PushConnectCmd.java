package com.simple.game.core.domain.cmd.push.game;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.game.ReqConnectCmd;

import lombok.Data;

@Data
public class PushConnectCmd extends PushGameCmd{
	private long playerId;
	private String nickname;
	private String headPic;
	
	@Override
	public int getCode() {
		return ReqConnectCmd.CODE + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
