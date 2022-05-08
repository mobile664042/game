package com.simple.game.server.repository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.simple.game.server.cmd.req.PageReq;
import com.simple.game.server.cmd.rtn.RtnResult;
import com.simple.game.server.dbEntity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class UserCacheDao extends BaseCacheDao<Long, User> {
	private static final AtomicLong INDEX  = new  AtomicLong(10000);
	protected LoadingCache<String, User> usernameCache = CacheBuilder.newBuilder()
            //设置并发级别为8，并发级别是指可以同时写缓存的线程数
            .concurrencyLevel(8)
            //设置缓存容器的初始容量为10
            .initialCapacity(10)
            //设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
            .maximumSize(300)
            //设置写缓存后n秒钟过期
            .expireAfterWrite(7, TimeUnit.DAYS)
            //只阻塞当前数据加载线程，其他线程返回旧值
            .refreshAfterWrite(7, TimeUnit.DAYS)
            //设置缓存的移除通知
            .removalListener(notification -> {
            	log.info(notification.getKey() + " 被移除,原因:" + notification.getCause());
            })
            //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
            .build(new CacheLoader<String, User> (){
    	        @Override
    	        public User load(String username) throws Exception {
    	            return null;
    	        }
    	    });
	
	
	public User getByUsername(String username) {
		try {
			return (User)usernameCache.get(username);
		} catch (Exception e) {
			return null;
		}
	}

	public long insert(User user) {
		long id = INDEX.incrementAndGet();
		user.setId(id);
		cache.put(id, user);
		usernameCache.put(user.getUsername(), user);
		return id;
	}

	public User getById(Long param) {
		try {
			return cache.get(param);
		} catch (ExecutionException e) {
			return null;
		}
	}

	public RtnResult<List<User>> searchPage(PageReq<String> req) {
		RtnResult<List<User>> rtnResult = new RtnResult<List<User>>();
		List<User> list = new ArrayList<User>();
		rtnResult.setData(list);
		rtnResult.setTotal(cache.size());
		
		List<Long> keyList = new ArrayList<Long>(cache.asMap().keySet());
		int from = req.getPageFrom() * req.getPageSize();
		int to = from + req.getPageSize();
		
		for(int i = from; i<to && i<keyList.size(); i++) {
			Long playerId = keyList.get(i);
			try {
				User user = (User)cache.get(playerId);
				list.add(user);
			} catch (ExecutionException e) {
			}
		}
		
		return rtnResult;
	}
	
	
	
}
