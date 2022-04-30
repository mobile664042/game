package com.simple.game.core.util;

import java.util.concurrent.ThreadFactory;

public class MyThreadFactory implements ThreadFactory{
	private int index;
	private String preName;

	public MyThreadFactory(String preName) {
		this.preName = preName;
	}

	@Override
	public Thread newThread(Runnable r) {
		++ index;
		String name = preName + "_" + index;
		return new Thread(r, name);
	}

}
