package com.simple.game.ddz.util;

/***
 * 最简的队列，用在游戏计算中
 * @author zhibozhang
 *
 * @param <T>
 */
public class SimpleQueue<T> {
	private final  T[] data;
	private int index = 0;
	private T last;
	
	@SuppressWarnings("unchecked")
	public SimpleQueue(int capacity) {
		data = (T[]) new Object[capacity];
	}
	
	public void push(T item) {
		if(index == data.length) {
			//如果满了,就移一位
			for(int i=0; i<data.length-1; i++) {
				data[i] = data[i+1];
			}
			index = data.length-1;
		}
		data[index] = item;
		
		if(item != null) {
			last = item;
		}
		
		++index;
	}
	public void clear() {
		for(int i=0; i<data.length; i++) {
			data[i] = null;
		}
		index = 0;
	}
	
	public boolean isNull() {
//		for(int i=0; i<=index; i++) {
		for(int i=0; i<index; i++) {
			if(data[i] != null) {
				return false;
			}
		}
		return true;
	}
	
	public T getCurrent() {
		return data[index];
	}
	public T getLast() {
		return last;
	}
	public T getFirst() {
		return data[0];
	}
	public T[] getData() {
		return data;
	}
}
