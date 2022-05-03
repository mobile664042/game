package com.simple.game.core.domain.cmd.push.game;

import lombok.Data;

@Data
public class PushConnectCmd extends PushGameCmd{
	private long playerId;
	private String nickname;
	private String headPic;
	
	@Override
	public int getCode() {
		return 1101007;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
