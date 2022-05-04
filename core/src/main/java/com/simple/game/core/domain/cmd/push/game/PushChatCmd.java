package com.simple.game.core.domain.cmd.push.game;

import com.simple.game.core.domain.ext.Chat;

import lombok.Data;

@Data
public class PushChatCmd extends PushGameCmd{
	public final static int CODE = 1101006;
	
	private long playerId;
	private String nickname;
	private String headPic;
	private Chat chat;
	
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
