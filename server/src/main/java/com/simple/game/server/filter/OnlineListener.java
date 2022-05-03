package com.simple.game.server.filter;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.simple.game.server.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebListener
@Component
public class OnlineListener implements HttpSessionListener, HttpSessionAttributeListener {
	@Autowired
	private UserService userService;

	public void sessionCreated(HttpSessionEvent se) {
		log.info("产生了一个会话了");
    }
	
	// 销毁一个session时触发此操作
	public void sessionDestroyed(HttpSessionEvent se) {
		userService.logout(se.getSession());
	}

}