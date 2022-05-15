package com.simple.game.ddz.domain.cmd.push.game.notify;

import com.simple.game.core.domain.cmd.push.PushCmd;

import lombok.Data;

/***
 * 跳过当前局
 * 
 * @author Administrator
 *
 */
@Data
public class NotifyGameSkipCmd extends PushCmd{

	@Override
	public int getCmd() {
		return 151005 + PushCmd.NOTIFY_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
