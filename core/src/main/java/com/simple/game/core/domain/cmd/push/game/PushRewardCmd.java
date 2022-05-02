package com.simple.game.core.domain.cmd.push.game;

import java.util.List;

import com.simple.game.core.domain.ext.Gift;

import lombok.Data;

@Data
public class PushRewardCmd extends PushGameCmd{
	private long playerId;
	private String nickname;
	private List<Integer> positionList;
	private Gift gift;
	
	@Override
	public int getCode() {
		return 1101009;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
