package com.simple.game.ddz.domain.cmd.push.game.notify;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqDoubledCmd;

import lombok.Data;

@Data
public class NotifyDoubledCmd extends PushCmd{
	@Override
	public int getCmd() {
		return ReqDoubledCmd.CMD + PushCmd.NOTIFY_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
