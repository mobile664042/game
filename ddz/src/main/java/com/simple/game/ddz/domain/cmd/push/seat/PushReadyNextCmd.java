package com.simple.game.ddz.domain.cmd.push.seat;

import java.util.ArrayList;
import java.util.List;

import com.simple.game.core.domain.cmd.push.seat.PushSeatCmd;

import lombok.Data;

@Data
public class PushReadyNextCmd extends PushSeatCmd{
	private List<Integer> cards = new ArrayList<Integer>();
	

	@Override
	public int getCode() {
		return 1151001;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
