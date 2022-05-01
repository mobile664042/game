package com.simple.game.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestLock {
	
	public static void main(String[] args) {
		MytestItem o1 = new MytestItem("张三", 1);
		MytestItem o2 = new MytestItem("李2", 2);
		MytestItem o3 = new MytestItem("王2", 4);
		MytestItem o4 = new MytestItem("马2", 2);
		
		testLock(o1, o2, o3, o4);
	}
	
	
	public static void testLock(MytestItem ...array) {
		LockList lockList = createLockList(array);
		lockList.lock();
		System.out.println("我要开始做野了");
		lockList.unlock();
		System.out.println("任务结束");
	}
	static class MytestItem implements MyLock{
		String name;
		int id;
		final transient Lock lock = new ReentrantLock();
		
		public MytestItem(String name, int id) {
			this.name = name;
			this.id = id;
		}
		public int hashCode() {
			return id;
		}
		public String toString() {
			return "MytestItem [name=" + name + ", id=" + id + "]";
		}
		public Lock getMyLock() {
			return lock;
		}
	}
	
	
	
	
	public static LockList createLockList(MyLock ... objects) {
		LockList lockList = new LockList();
		if(objects == null || objects.length == 0) {
			return lockList;
		}
		
		List<MyLock> temp = Arrays.asList(objects);
		Collections.sort(temp, new MyComparator());
		lockList.list.addAll(temp);
		return lockList;
	}
	static class LockList{
		private final List<MyLock> list= new ArrayList<MyLock>(4);
		public void lock() {
			for(int i=0; i<list.size(); i++) {
				MyLock item = list.get(i);
				item.getMyLock().lock();
				System.out.println(item + ":加锁");
			}
		}
		public void unlock() {
			for(int i=list.size()-1; i>=0; i--) {
				MyLock item = list.get(i);
				item.getMyLock().unlock();
				System.out.println(item + ":解锁");
			}		
		}
	}

	static class MyComparator implements Comparator<MyLock> {
		@Override
		public int compare(MyLock o1, MyLock o2) {
			if(o1.hashCode() > o2.hashCode()) {
				return 1;
			}
			else if(o1.hashCode() < o2.hashCode()) {
				return -1;
			}
			//暂不考虑哈希code相同的对象（简化处理），
			//如果code相同按照加入列表的先后顺序
			return -1;
		}
	}
	static interface MyLock{
		Lock getMyLock();
	}
}
