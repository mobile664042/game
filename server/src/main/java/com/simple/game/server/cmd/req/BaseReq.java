package com.simple.game.server.cmd.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("基础请求参数(解决同一json请求格式问题)")
public class BaseReq<T> {
	
	/***
	 * 查詢參數，可能為空
	 */
	private T param;
    
	
}
