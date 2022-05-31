package com.simple.game.ddz.domain.cmd.rtn.seat;

import java.util.List;

import com.simple.game.core.domain.cmd.rtn.RtnCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqRobLandlordCmd;

import lombok.Data;

@Data
public class RtnRobLandlordCmd extends RtnCmd{
	
	/***底牌的手牌****/
	private List<Integer> cards;
	
	/***最终手牌，为了客户偷懒排序****/
	private List<Integer> finalCards;
	
	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCmd() {
		return ReqRobLandlordCmd.CMD;
	}
}
