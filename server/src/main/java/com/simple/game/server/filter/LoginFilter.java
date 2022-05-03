package com.simple.game.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.simple.game.server.cmd.rtn.RtnResult;
import com.simple.game.server.constant.MyConstant;

public class LoginFilter implements Filter{

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		OnlineAccount onlineAccount = (OnlineAccount)request.getSession().getAttribute(MyConstant.SESSION_KEY);
		
		if(request.getServletPath().startsWith(MyConstant.SESSION_PATH_PREFIX)) {
			if(onlineAccount == null) {
				HttpServletResponse response = (HttpServletResponse)resp;
				RtnResult<?> rtn = RtnResult.invalidSession();
				String json = JSON.toJSONString(rtn);
				response.getWriter().write(json);
				return;
			}
		}

		
		try {
			OnlineAccount.cache(onlineAccount);
			chain.doFilter(request, resp);
		}
		finally {
			OnlineAccount.clear();
		}
	}

}
