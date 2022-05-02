package com.simple.game.core.domain.cmd.push.game.notify;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.ext.Chat;

import lombok.Data;

@Data
public class PushSysChatCmd extends PushCmd{
	private Chat chat;
	
	@Override
	public int getCode() {
		return 2101003;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
