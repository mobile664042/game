package com.simple.game.server.controller;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.simple.game.server.dbEntity.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
 
//https://blog.csdn.net/weixin_42165041/article/details/81077100?spm=1001.2101.3001.6661.1&utm_medium=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1.pc_relevant_aa&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1.pc_relevant_aa&utm_relevant_index=1
@RestController
@RequestMapping("/person")
@Api(tags = "人员接口",description="人员文档说明",hidden=true)
public class PersonController {
 
	@RequestMapping(value="selectAll",method=RequestMethod.POST)
	@ApiOperation(value="查询所有的人员",notes="查询所有的人员接口说明")
	@ApiImplicitParams({
		@ApiImplicitParam(name="month",value="年月，格式为：201801",dataType="String", paramType = "query"),
		@ApiImplicitParam(name="pageSize",value="页码",dataType="String", paramType = "query"),
		@ApiImplicitParam(name="pageNum",value="每页条数",dataType="String", paramType = "query"),
		@ApiImplicitParam(name="empName",value="业务员名称",dataType="String", paramType = "query"),
		@ApiImplicitParam(name="orderType",value="排序类型",dataType="String", paramType = "query"),
	})
	@ApiResponse(response=User.class, code = 200, message = "接口返回对象参数")
	public List<User> selectAll(HttpServletRequest request) {
		return new ArrayList<User>();
	}
	
	@RequestMapping(value="findById",method=RequestMethod.POST)
	@ResponseBody
	public User findById(Integer id) {
		return new User();
	}
	
}