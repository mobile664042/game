package com.simple.game.server.controller.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.game.server.cmd.req.BaseReq;
import com.simple.game.server.cmd.rtn.RtnResult;
import com.simple.game.server.cmd.rtn.game.DdzDeskRtn;
import com.simple.game.server.cmd.rtn.game.DdzKindRtn;
import com.simple.game.server.constant.MyConstant;
import com.simple.game.server.service.MyDdzGameService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(MyConstant.SESSION_PATH_PREFIX + "/client/game/ddz")
@Api(tags = "斗地主接口")
public class MyDdzGameController {

    @Autowired
    private MyDdzGameService myDdzGameService;

    @RequestMapping(value="/getKindList",method=RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value="获取玩法类型",notes="获取玩法类型")
    public RtnResult<List<DdzKindRtn>> getKindList() {
    	List<DdzKindRtn> result = myDdzGameService.getKindList();
        return RtnResult.success(result);
    }


    @RequestMapping(value="/getDeskList",method=RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value="获取游戏桌列表",notes="获取游戏桌列表")
    public RtnResult<List<DdzDeskRtn>> getDeskList(@RequestBody BaseReq<Integer> req) {
    	List<DdzDeskRtn> result = myDdzGameService.getDeskList(req.getParam());
        return RtnResult.success(result);
    }


}
