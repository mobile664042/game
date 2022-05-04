package com.simple.game.core.domain.cmd.rtn.game;

import com.simple.game.core.domain.cmd.req.game.ReqJoinCmd;
import com.simple.game.core.domain.cmd.rtn.RtnCmd;

import lombok.Data;

@Data
public class RtnGameInfoCmd extends RtnCmd{
	/***所在游戏中的定位(可能为空)***/
	private Integer playKind;
	
	/***所在游戏中的地址
	 * 用户解决重新进入的问题
	 * 
	 * 玩法、桌号、席位三层地址
	 * @45@78@1
	 * ***/
	private String address;
	
	
	/***
	 * 暂停时长(毫秒)
	 */
	private long pauseMs;
	
	@Override
	public int getCode() {
		return ReqJoinCmd.CODE;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
