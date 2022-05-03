package com.simple.game.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.simple.game.server.cmd.req.user.LoginReq;
import com.simple.game.server.cmd.rtn.RtnResult;
import com.simple.game.server.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "登录/退出接口")
public class ClientLoginController {

    @Autowired
    private UserService userService;


    @RequestMapping(value="/client/user/login",method=RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value="登录",notes="登录")
    public RtnResult<String> login(HttpServletRequest request, @RequestBody LoginReq req) {
    	String loginToken = userService.login(request, req);
        return RtnResult.success(loginToken);
    }

    @RequestMapping(value="/client/user/logout",method=RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value="退出",notes="退出")
    public RtnResult<String> logout(HttpServletRequest request){
    	userService.logout(request.getSession());
        return RtnResult.success("ok");
    }


}
