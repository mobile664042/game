package com.simple.game.ddz.domain.cmd.push.seat;

import java.util.ArrayList;
import java.util.List;

import com.simple.game.core.domain.cmd.push.seat.PushSeatCmd;

import lombok.Data;

@Data
public class PushPlayCardCmd extends PushSeatCmd{
	public final static int CODE = 1151003;
	
	private List<Integer> cards = new ArrayList<Integer>();
	

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
