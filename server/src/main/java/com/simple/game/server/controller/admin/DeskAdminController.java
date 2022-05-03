package com.simple.game.server.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.simple.game.server.cmd.req.desk.BuildReq;
import com.simple.game.server.cmd.req.desk.DestroyReq;
import com.simple.game.server.cmd.req.desk.PauseReq;
import com.simple.game.server.cmd.req.desk.ResumeReq;
import com.simple.game.server.cmd.rtn.RtnResult;
import com.simple.game.server.constant.MyConstant;
import com.simple.game.server.service.MyDdzGameService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController(MyConstant.SESSION_PATH_PREFIX)
@Api(tags = "游戏桌管理接口")
public class DeskAdminController {

	@Autowired
	private MyDdzGameService myDdzGameService;

	@RequestMapping(value = "/admin/game/ddz/buildDesk", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "创建斗地主游戏桌", notes = "创建斗地主游戏桌")
	public RtnResult<String> buildDdzDesk(BuildReq req) {
		myDdzGameService.buildDdzDesk(req);
		return RtnResult.success("ok");
	}
	
	@RequestMapping(value = "/admin/game/ddz/destroyDesk", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "销毁斗地主游戏桌", notes = "销毁斗地主游戏桌")
	public RtnResult<String> destroyDdzDesk(DestroyReq req) {
		myDdzGameService.destroyDesk(req);
		return RtnResult.success("ok");
	}


	@RequestMapping(value = "/admin/game/ddz/pauseDesk", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "暂停斗地主游戏桌", notes = "暂停销毁斗地主游戏桌")
	public RtnResult<String> pauseDdzDesk(PauseReq req) {
		myDdzGameService.pauseDdzDesk(req);
		return RtnResult.success("ok");
	}

	@RequestMapping(value = "/admin/game/ddz/resumeDesk", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "恢复斗地主游戏桌", notes = "恢复斗地主游戏桌")
	public RtnResult<String> resumeDdzDesk(ResumeReq req) {
		myDdzGameService.resumeDdzDesk(req);
		return RtnResult.success("ok");
	}

	
}
