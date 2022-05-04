package com.simple.game.core.domain.cmd.push.game;

import java.util.List;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.req.game.ReqRewardCmd;
import com.simple.game.core.domain.ext.Gift;

import lombok.Data;

@Data
public class PushRewardCmd extends PushGameCmd{
	private long playerId;
	private String nickname;
	private String headPic;
	private List<Integer> positionList;
	private Gift gift;
	
	@Override
	public int getCode() {
		return ReqRewardCmd.CODE + PushCmd.PUSH_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
