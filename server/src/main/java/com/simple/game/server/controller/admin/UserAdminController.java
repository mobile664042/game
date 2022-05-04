package com.simple.game.server.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.simple.game.server.cmd.req.BaseReq;
import com.simple.game.server.cmd.req.PageReq;
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
@RestController(MyConstant.SESSION_PATH_PREFIX)
@Api(tags = "用戶管理接口")
public class UserAdminController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/admin/user/searchPage", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "获取玩用戶分頁列表", notes = "获取玩用戶分頁列表")
	public RtnResult<List<EntityRtn>> searchPage(PageReq<String> req) {
		log.info("收到请求:{}", req);
		RtnResult<List<EntityRtn>> result = userService.searchPage(req);
		return result;
	}

	@RequestMapping(value = "/admin/user/kickout", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "给玩家踢下线", notes = "给玩家踢下线")
	public RtnResult<String> kickout(KickoutReq req) {
		log.info("收到请求:{}", req);
		userService.kickout(req);
		return RtnResult.success("ok");
	}

	@RequestMapping(value = "/admin/user/getDetail", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "获取玩家的详细信息", notes = "获取玩家的详细信息")
	public RtnResult<DetailRtn> getDetail(BaseReq<Long> req) {
		log.info("收到请求:{}", req);
		DetailRtn rtn = userService.getDetail(req);
		return RtnResult.success(rtn);
	}
	
	@RequestMapping(value = "/admin/user/chat", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "给用户发信息", notes = "给用户发信息")
	public RtnResult<String> chat(ChatReq req) {
		log.info("收到请求:{}", req);
		userService.chat(req);
		return RtnResult.success("ok");
	}
	
}
