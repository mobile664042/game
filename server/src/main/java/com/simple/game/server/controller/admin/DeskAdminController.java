package com.simple.game.server.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.game.server.cmd.req.desk.BuildReq;
import com.simple.game.server.cmd.req.desk.DestroyReq;
import com.simple.game.server.cmd.req.desk.PauseReq;
import com.simple.game.server.cmd.req.desk.ResumeReq;
import com.simple.game.server.cmd.rtn.RtnResult;
import com.simple.game.server.constant.MyConstant;
import com.simple.game.server.service.MyDdzGameService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(MyConstant.SESSION_PATH_PREFIX + "/admin/game/ddz")
@Api(tags = "游戏桌管理接口")
public class DeskAdminController {

	@Autowired
	private MyDdzGameService myDdzGameService;

	@RequestMapping(value = "/buildDesk", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "创建斗地主游戏桌", notes = "创建斗地主游戏桌")
	public RtnResult<String> buildDdzDesk(BuildReq req) {
		log.info("收到请求{}", req);
		myDdzGameService.buildDdzDesk(req);
		return RtnResult.success("ok");
	}
	
	@RequestMapping(value = "/destroyDesk", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "销毁斗地主游戏桌", notes = "销毁斗地主游戏桌")
	public RtnResult<String> destroyDdzDesk(DestroyReq req) {
		log.info("收到请求{}", req);
		myDdzGameService.destroyDesk(req);
		return RtnResult.success("ok");
	}


	@RequestMapping(value = "/pauseDesk", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "暂停斗地主游戏桌", notes = "暂停销毁斗地主游戏桌")
	public RtnResult<String> pauseDdzDesk(PauseReq req) {
		log.info("收到请求{}", req);
		myDdzGameService.pauseDdzDesk(req);
		return RtnResult.success("ok");
	}

	@RequestMapping(value = "/resumeDesk", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "恢复斗地主游戏桌", notes = "恢复斗地主游戏桌")
	public RtnResult<String> resumeDdzDesk(ResumeReq req) {
		log.info("收到请求{}", req);
		myDdzGameService.resumeDdzDesk(req);
		return RtnResult.success("ok");
	}

	
}
