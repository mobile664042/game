package com.simple.game.core.util;

public class SimpleUtil {
	public static final boolean contain(long id, long ...set){
		if(set == null || set.length == 0) {
			return false;
		}
		for(long temp : set) {
			if(temp == id) {
				return true;
			}
		}
		return false;
	}
}
