package com.simple.game.server.controller.client;

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


    
}
