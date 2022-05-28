package com.simple.game.core.domain.cmd.req.game;

import java.util.List;

import com.simple.game.core.domain.cmd.push.game.PushRewardCmd;
import com.simple.game.core.domain.ext.Gift;

import lombok.Data;

@Data
public class ReqRewardCmd extends ReqGameCmd{
	public final static int CMD = 101009;
	
	private List<Integer> positionList;
	private Gift gift;
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushRewardCmd valueOfPushRewardCmd() {
		PushRewardCmd pushCmd = new PushRewardCmd();
//		pushCmd.setDeskNo(deskNo);
//		pushCmd.setPlayKind(playKind);
//		pushCmd.setPlayerId(playerId);
		pushCmd.setPositionList(positionList);
		pushCmd.setGift(gift);
		return pushCmd;
	}
}
