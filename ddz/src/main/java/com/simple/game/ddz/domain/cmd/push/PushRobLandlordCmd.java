package com.simple.game.ddz.domain.cmd.push;

import java.util.ArrayList;
import java.util.List;

import com.simple.game.core.domain.cmd.push.PushCmd;

import lombok.Data;

@Data
public class PushRobLandlordCmd extends PushCmd{
	private int position;
	private List<Integer> cards = new ArrayList<Integer>();
	

	@Override
	public int getCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
