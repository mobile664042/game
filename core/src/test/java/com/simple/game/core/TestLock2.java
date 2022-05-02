package com.simple.game.core;

import java.util.concurrent.atomic.AtomicReference;

public class TestLock2 {
	
	public static void main(String[] args) {
		AtomicReference<String> a = new AtomicReference<String>();
		System.out.println(a.get());
		
		a.set("mygod");
		
		if(a.compareAndSet(null, "yourgod")) {
			System.out.println(a.get());
		}
		System.out.println("over");
		System.out.println(a.get());
		
	}
	
}
