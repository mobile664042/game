package com.simple.game.core.domain.cmd.push.game;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.game.ReqChatCmd;
import com.simple.game.core.domain.ext.Chat;

import lombok.Data;

@Data
public class PushChatCmd extends PushGameCmd{
	private long playerId;
	private String nickname;
	private String headPic;
	private Chat chat;
	
	@Override
	public int getCmd() {
		return ReqChatCmd.CMD + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
