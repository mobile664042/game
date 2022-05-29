package com.simple.game.ddz.domain.ruler;

import java.util.Comparator;

import com.simple.game.core.domain.dto.constant.SCard;

public class PokerComparator implements Comparator<SCard>{

	@Override
	public int compare(SCard o1, SCard o2) {
		if(o1.getValue() > o2.getValue()) {
			return 1;
		}
		
		if(o1.getValue() < o2.getValue()) {
			return -1;
		}
		return o1.getOrder()-o2.getOrder();
	}

}
