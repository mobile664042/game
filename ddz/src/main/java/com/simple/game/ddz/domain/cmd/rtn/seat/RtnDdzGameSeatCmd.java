package com.simple.game.ddz.domain.cmd.rtn.seat;

import java.util.List;

import com.simple.game.core.domain.cmd.rtn.seat.RtnGameSeatInfoCmd;

import lombok.Data;

@Data
public class RtnDdzGameSeatCmd extends RtnGameSeatInfoCmd{
	private boolean ready = true;
	
	/***当前轮的跳过次数****/
	private int skipCount;
	
	/***当前轮的跳过次数****/
	private int timeoutCount;
	
	/***剩余的手牌****/
	private List<Integer> cards;
	
	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public void copy(RtnGameSeatInfoCmd parent) {
		this.setMaster(parent.getMaster());;
		this.setNextMaster(parent.getNextMaster());
		this.setStopOnlooker(parent.isStopOnlooker());
		this.setStopAssistant(parent.isStopAssistant());
		this.setBroadcasting(parent.isBroadcasting());
		this.setApplyBroadcasted(parent.isApplyBroadcasted());
		this.setPosition(parent.getPosition());
		this.setSeatPost(parent.getSeatPost());
	}
}
