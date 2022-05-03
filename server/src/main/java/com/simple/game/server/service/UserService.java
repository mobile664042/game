package com.simple.game.server.service;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public void login(HttpServletRequest request, LoginReq req) {
		User old = userCacheDao.getByUsername(req.getUsername());
		if(old == null) {
			throw new BizException(req.getUsername() + "的用户名不存在，是不是要注册一个呢");
		}
		if(!req.getPassword().equals(old)) {
			throw new BizException(req.getUsername() + "的密码不对哦");
		}
		OnlineAccount onlineAccount = OnlineAccount.valueOf(old);
		request.getSession().setAttribute(MyConstant.SESSION_KEY, onlineAccount);
		log.info("{}用户登录了", req.getUsername());
	}

	public void logout(HttpServletRequest request) {
		request.getSession().removeAttribute(MyConstant.SESSION_KEY);
	}
}
