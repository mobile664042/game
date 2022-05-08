package com.simple.game.server.controller.client;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.game.server.cmd.req.user.AddReq;
import com.simple.game.server.cmd.rtn.RtnResult;
import com.simple.game.server.constant.MyConstant;
import com.simple.game.server.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(MyConstant.SESSION_PATH_PREFIX + "/client/user")
@Api(tags = "用户接口")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value="/register",method=RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value="注册",notes="注册")
    public RtnResult<Long> register(@RequestBody AddReq req) {
    	log.info("收到请求:{}", req);
        Long result = userService.register(req);
        return RtnResult.success(result);
    }


    @PostConstruct
    void initUser() {
    	log.info("准备初使化10个默认用户");
    	{
    		AddReq req = new AddReq();
    		req.setUsername("zhangsan");
    		req.setPassword("123456");
    		req.setNickname("张三");
    		req.setHeadPic(0);
    		req.setSex(1);
    		req.setTelphone("15919604042");
    		userService.register(req);
    	}
    	{
    		AddReq req = new AddReq();
    		req.setUsername("lisi");
    		req.setPassword("123456");
    		req.setNickname("李四");
    		req.setHeadPic(1);
    		req.setSex(2);
    		req.setTelphone("15919604043");
    		userService.register(req);
    	}
    	{
    		AddReq req = new AddReq();
    		req.setUsername("wangwu");
    		req.setPassword("123456");
    		req.setNickname("王五");
    		req.setHeadPic(3);
    		req.setSex(2);
    		req.setTelphone("15919604044");
    		userService.register(req);
    	}
    	{
    		AddReq req = new AddReq();
    		req.setUsername("maliu");
    		req.setPassword("123456");
    		req.setNickname("马六");
    		req.setHeadPic(4);
    		req.setSex(1);
    		req.setTelphone("15919604045");
    		userService.register(req);
    	}
    	{
    		AddReq req = new AddReq();
    		req.setUsername("qianqi");
    		req.setPassword("123456");
    		req.setNickname("钱七");
    		req.setHeadPic(5);
    		req.setSex(1);
    		req.setTelphone("15919604046");
    		userService.register(req);
    	}
    	{
    		AddReq req = new AddReq();
    		req.setUsername("zhaoba");
    		req.setPassword("123456");
    		req.setNickname("赵八");
    		req.setHeadPic(6);
    		req.setSex(2);
    		req.setTelphone("15919604047");
    		userService.register(req);
    	}
    	{
    		AddReq req = new AddReq();
    		req.setUsername("fushi");
    		req.setPassword("123456");
    		req.setNickname("富士");
    		req.setHeadPic(7);
    		req.setSex(1);
    		req.setTelphone("15919604048");
    		userService.register(req);
    	}
    	{
    		AddReq req = new AddReq();
    		req.setUsername("xxxo");
    		req.setPassword("123456");
    		req.setNickname("小测");
    		req.setHeadPic(8);
    		req.setSex(2);
    		req.setTelphone("15919604049");
    		userService.register(req);
    	}
    	{
    		AddReq req = new AddReq();
    		req.setUsername("xooo");
    		req.setPassword("123456");
    		req.setNickname("大测");
    		req.setHeadPic(9);
    		req.setSex(1);
    		req.setTelphone("15919604050");
    		userService.register(req);
    	}
    	log.info("初使化默认用户完成");
    }
}
