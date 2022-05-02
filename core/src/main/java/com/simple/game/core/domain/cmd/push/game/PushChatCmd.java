package com.simple.game.core.domain.cmd.push.game;

import com.simple.game.core.domain.ext.Chat;

import lombok.Data;

@Data
public class PushChatCmd extends PushGameCmd{
	private long playerId;
	private String nickname;
	private Chat chat;
	
	@Override
	public int getCode() {
		return 1101006;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
