package com.simple.game.server.service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.simple.game.core.exception.BizException;
import com.simple.game.ddz.domain.service.DdzAdminService;
import com.simple.game.server.cmd.req.BaseReq;
import com.simple.game.server.cmd.req.PageReq;
import com.simple.game.server.cmd.req.user.AddReq;
import com.simple.game.server.cmd.req.user.ChatReq;
import com.simple.game.server.cmd.req.user.KickoutReq;
import com.simple.game.server.cmd.req.user.LoginReq;
import com.simple.game.server.cmd.rtn.RtnResult;
import com.simple.game.server.cmd.rtn.user.DetailRtn;
import com.simple.game.server.cmd.rtn.user.DetailRtn.GameItemInfo;
import com.simple.game.server.cmd.rtn.user.EntityRtn;
import com.simple.game.server.constant.MyConstant;
import com.simple.game.server.dbEntity.User;
import com.simple.game.server.filter.OnlineAccount;
import com.simple.game.server.filter.OnlineAccount.GameOnlineInfo;
import com.simple.game.server.repository.UserCacheDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService{
	/***
	 * key --> playerId
	 * value -->online_cache.key
	 */
	private static final ConcurrentHashMap<Long, String> online_id_map = new ConcurrentHashMap<Long, String>();
	/***
	 * key -->loginToken
	 */
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
            	OnlineAccount onlineAccount = (OnlineAccount)notification.getValue();
            	online_id_map.remove(onlineAccount.getUser().getId());
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
	
	@Autowired
	private DdzAdminService ddzAdminService;
	
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
		online_id_map.put(onlineAccount.getUser().getId(), loginToken);
		log.info("{}用户登录了,loginToken={}", req.getUsername(), loginToken);
		return loginToken;
	}

	public void logout(HttpSession httpSession) {
		OnlineAccount onlineAccount = (OnlineAccount)httpSession.getAttribute(MyConstant.SESSION_KEY);
		if(onlineAccount != null) {
			online_cache.invalidate(onlineAccount.getLoginToken());
			//online_id_map.remove(onlineAccount.getUser().getId());
			httpSession.removeAttribute(MyConstant.SESSION_KEY);
			log.info("{}用户走了,loginToken={}", onlineAccount.getUser().getUsername(), onlineAccount.getLoginToken());
		}
	}
	
	public OnlineAccount getOnlineAccount(String loginToken) {
		try {
			return online_cache.get(loginToken);
		}
		catch(Exception e) {
			log.debug("loginToken={}用户不在线", loginToken);
			return null;
		}
	}

	public RtnResult<List<EntityRtn>> searchPage(PageReq<String> req) {
		RtnResult<List<User>> temp = userCacheDao.searchPage(req);
		
		RtnResult<List<EntityRtn>> out = new RtnResult<List<EntityRtn>>();
		List<EntityRtn> list = new ArrayList<EntityRtn>();
		for(User user: temp.getData()) {
			list.add(EntityRtn.valueOfUser(user));
		}
		out.setData(list);
		out.setTotal(temp.getTotal());
		
		return out;
	}

	/***
	 * 踢下线，
	 * 将来还需要考虑不只有一个参数的情况
	 * @param req
	 */
	public void kickout(KickoutReq req) {
		String loginToken = online_id_map.get(req.getPlayerId());
		if(loginToken == null) {
			throw new BizException("用户不在线啊！");
		}
		OnlineAccount onlineAccount = getOnlineAccount(loginToken);
		if(onlineAccount == null) {
			log.warn("有严重bug，loginToken={}没找到在信息息", loginToken);
			throw new BizException("用户不在线啊2！");
		}
		if(MyConstant.DDZ.equals(req.getGameCode())) {
			GameOnlineInfo gameOnlineInfo = onlineAccount.getOnlineWebSocket().get(MyConstant.DDZ);
			if(gameOnlineInfo != null) {
				ddzAdminService.kickout(gameOnlineInfo.getPlayKind(), gameOnlineInfo.getDeskNo(), req.getPlayerId());
			}
		}
	}
	
	public void chat(ChatReq req) {
		String loginToken = online_id_map.get(req.getPlayerId());
		if(loginToken == null) {
			throw new BizException("用户不在线啊！");
		}
		OnlineAccount onlineAccount = getOnlineAccount(loginToken);
		if(onlineAccount == null) {
			log.warn("有严重bug，loginToken={}没找到在信息息", loginToken);
			throw new BizException("用户不在线啊2！");
		}
		
		if(MyConstant.DDZ.equals(req.getGameCode())) {
			GameOnlineInfo gameOnlineInfo = onlineAccount.getOnlineWebSocket().get(MyConstant.DDZ);
			if(gameOnlineInfo != null) {
				ddzAdminService.chat(gameOnlineInfo.getPlayKind(), gameOnlineInfo.getDeskNo(), req.getPlayerId(), req.getChat());
			}
		}
	}


	public DetailRtn getDetail(BaseReq<Long> req) {
		User old = userCacheDao.getById(req.getParam());
		if(old == null) {
			return null;
		}
		
		DetailRtn rtn = new DetailRtn();
		rtn.setEntityRtn(EntityRtn.valueOfUser(old));
		
		String loginToken = online_id_map.get(req.getParam());
		if(loginToken == null) {
			return rtn;
		}
		OnlineAccount onlineAccount = getOnlineAccount(loginToken);
		if(onlineAccount == null) {
			log.warn("有严重bug，loginToken={}没找到在信息息", loginToken);
			return rtn;
		}
		List<GameItemInfo> gameList = new ArrayList<GameItemInfo>();
		rtn.setGameList(gameList);
		for(String gameCode : onlineAccount.getOnlineWebSocket().keySet()) {
			GameItemInfo itemInfo = new GameItemInfo();
			gameList.add(itemInfo);
			
			GameOnlineInfo o = onlineAccount.getOnlineWebSocket().get(gameCode);
			itemInfo.setGameCode(gameCode);
			itemInfo.setDeskNo(o.getDeskNo());
			itemInfo.setPlayKind(o.getPlayKind());
		}
		return rtn;
	}


}
