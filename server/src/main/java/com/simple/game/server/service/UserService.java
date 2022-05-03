package com.simple.game.server.service;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.simple.game.core.exception.BizException;
import com.simple.game.server.cmd.req.user.AddReq;
import com.simple.game.server.cmd.req.user.LoginReq;
import com.simple.game.server.constant.MyConstant;
import com.simple.game.server.dbEntity.User;
import com.simple.game.server.filter.OnlineAccount;
import com.simple.game.server.repository.UserCacheDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService{
	private static final LoadingCache<String, OnlineAccount> online_cache = CacheBuilder.newBuilder()
            //设置并发级别为8，并发级别是指可以同时写缓存的线程数
            .concurrencyLevel(8)
            //设置缓存容器的初始容量为10
            .initialCapacity(10)
            //设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
            .maximumSize(300)
            //设置写缓存后n秒钟过期
            .expireAfterWrite(17, TimeUnit.SECONDS)
            //只阻塞当前数据加载线程，其他线程返回旧值
            .refreshAfterWrite(13, TimeUnit.SECONDS)
            //设置缓存的移除通知
            .removalListener(notification -> {
            	log.info(notification.getKey() + " 被移除,原因:" + notification.getCause());
            })
            //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
            .build(new CacheLoader<String, OnlineAccount> (){
    	        @Override
    	        public OnlineAccount load(String key) throws Exception {
    	            return null;
    	        }
    	    });
	
	
	@Autowired
	private UserCacheDao userCacheDao;
	
	public Long register(AddReq req) {
		User old = userCacheDao.getByUsername(req.getUsername());
		if(old != null) {
			throw new BizException(req.getUsername() + "的用户名已存在，快换一个吧");
		}
		User user = req.valueOfUser();
		long id = userCacheDao.insert(user);
		log.info("注册了{}用户了", req.getUsername());
		return id;
	}

	/***
	 * @param request
	 * @param req
	 * @return loginToken
	 */
	public String login(HttpServletRequest request, LoginReq req) {
		User old = userCacheDao.getByUsername(req.getUsername());
		if(old == null) {
			throw new BizException(req.getUsername() + "的用户名不存在，是不是要注册一个呢");
		}
		if(!req.getPassword().equals(old.getPassword())) {
			throw new BizException(req.getUsername() + "的密码不对哦");
		}
		
		String loginToken = UUID.randomUUID().toString();
		OnlineAccount onlineAccount = OnlineAccount.valueOf(old, loginToken);
		request.getSession().setAttribute(MyConstant.SESSION_KEY, onlineAccount);
		
		online_cache.put(loginToken, onlineAccount);
		log.info("{}用户登录了,loginToken={}", req.getUsername(), loginToken);
		return loginToken;
	}

	public void logout(HttpSession httpSession) {
		OnlineAccount onlineAccount = (OnlineAccount)httpSession.getAttribute(MyConstant.SESSION_KEY);
		if(onlineAccount != null) {
			online_cache.invalidate(onlineAccount.getLoginToken());
			httpSession.removeAttribute(MyConstant.SESSION_KEY);
			log.info("{}用户走了,loginToken={}", onlineAccount.getUser().getUsername(), onlineAccount.getLoginToken());
		}
	}
	
	public OnlineAccount getOnlineAccount(String loginToken) {
		try {
			//online_cache.refresh(loginToken);
			return online_cache.get(loginToken);
		}
		catch(Exception e) {
			log.debug("loginToken={}用户不在线", loginToken);
			return null;
		}
	}
}
