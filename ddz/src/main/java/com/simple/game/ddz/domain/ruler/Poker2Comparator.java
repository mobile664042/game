package com.simple.game.ddz.domain.ruler;

import java.util.Comparator;

import com.simple.game.core.domain.dto.constant.SCard;

/****
 * 特殊的排序
 * @author Administrator
 *
 */
public class Poker2Comparator implements Comparator<SCard>{

	@Override
	public int compare(SCard o1, SCard o2) {
		if(o1.getSv() > o2.getSv()) {
			return 1;
		}
		
		if(o1.getSv() < o2.getSv()) {
			return -1;
		}
		return o1.getOrder()-o2.getOrder();
	}

}
