package com.simple.game.core.domain.cmd.push.game;

import java.util.List;

import com.simple.game.core.domain.ext.Chat;

import lombok.Data;

@Data
public class PushChatMultiCmd extends PushGameCmd{
	public final static int CODE = 1101010;
	private long playerId;
	private String nickname;
	private String headPic;
	private List<Integer> positionList;
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
