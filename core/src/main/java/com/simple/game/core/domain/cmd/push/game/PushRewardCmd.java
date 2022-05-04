package com.simple.game.core.domain.cmd.push.game;

import java.util.List;

import com.simple.game.core.domain.ext.Gift;

import lombok.Data;

@Data
public class PushRewardCmd extends PushGameCmd{
	public final static int CODE = 1101009;
	
	private long playerId;
	private String nickname;
	private List<Integer> positionList;
	private Gift gift;
	
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
