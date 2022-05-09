package com.simple.game.server.controller.admin;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.game.server.cmd.req.BaseReq;
import com.simple.game.server.cmd.req.PageReq;
import com.simple.game.server.cmd.req.user.AddReq;
import com.simple.game.server.cmd.req.user.ChatReq;
import com.simple.game.server.cmd.req.user.KickoutReq;
import com.simple.game.server.cmd.rtn.RtnResult;
import com.simple.game.server.cmd.rtn.user.DetailRtn;
import com.simple.game.server.cmd.rtn.user.EntityRtn;
import com.simple.game.server.constant.MyConstant;
import com.simple.game.server.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(MyConstant.SESSION_PATH_PREFIX + "/admin/user")
@Api(tags = "用戶管理接口")
public class UserAdminController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/searchPage", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "获取玩用戶分頁列表", notes = "获取玩用戶分頁列表")
	public RtnResult<List<EntityRtn>> searchPage(@RequestBody PageReq<String> req) {
		log.info("收到请求:{}", req);
		RtnResult<List<EntityRtn>> result = userService.searchPage(req);
		return result;
	}

	@RequestMapping(value = "/kickout", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "给玩家踢下线", notes = "给玩家踢下线")
	public RtnResult<String> kickout(@RequestBody KickoutReq req) {
		log.info("收到请求:{}", req);
		userService.kickout(req);
		return RtnResult.success("ok");
	}

	@RequestMapping(value = "/getDetail", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "获取玩家的详细信息(id)", notes = "获取玩家的详细信息(id)")
	public RtnResult<DetailRtn> getDetail(@RequestBody BaseReq<Long> req) {
		log.info("收到请求:{}", req);
		DetailRtn rtn = userService.getDetail(req);
		return RtnResult.success(rtn);
	}
	
	@RequestMapping(value = "/getDetailByUsername", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "获取玩家的详细信息(username)", notes = "获取玩家的详细信息(username)")
	public RtnResult<DetailRtn> getDetailByUsername(@RequestBody BaseReq<String> req) {
		log.info("收到请求:{}", req);
		DetailRtn rtn = userService.getDetailByUsername(req);
		return RtnResult.success(rtn);
	}
	
	@RequestMapping(value = "/chat", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "给用户发信息", notes = "给用户发信息")
	public RtnResult<String> chat(@RequestBody ChatReq req) {
		log.info("收到请求:{}", req);
		userService.chat(req);
		return RtnResult.success("ok");
	}
	
	@PostConstruct
    void initTestUser() {
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
