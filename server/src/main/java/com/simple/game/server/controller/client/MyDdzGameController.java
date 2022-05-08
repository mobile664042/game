package com.simple.game.server.controller.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.game.server.cmd.rtn.RtnResult;
import com.simple.game.server.cmd.rtn.game.KindRtn;
import com.simple.game.server.constant.MyConstant;
import com.simple.game.server.service.MyDdzGameService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(MyConstant.SESSION_PATH_PREFIX + "/client/game")
@Api(tags = "斗地主接口")
public class MyDdzGameController {

    @Autowired
    private MyDdzGameService myDdzGameService;

    @RequestMapping(value="/getKindList",method=RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value="获取玩法类型",notes="获取玩法类型")
    public RtnResult<List<KindRtn>> getKindList() {
    	List<KindRtn> result = myDdzGameService.getKindList();
        return RtnResult.success(result);
    }


}
