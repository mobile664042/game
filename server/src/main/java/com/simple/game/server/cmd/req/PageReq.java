package com.simple.game.server.cmd.req;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("分頁查詢")
public class PageReq<T> extends BaseReq<T>{
	
	@Min(1)
	@ApiModelProperty(value = "起始頁面，從0開始", required = true)
    private int pageFrom;
    
	@Max(1000)
	@ApiModelProperty(value = "頁面大小", required = true)
    private int pageSize;
	
	@ApiModelProperty(value = "開始時間，時間毫秒值")
	private Long startTime;
	
	@ApiModelProperty(value = "結束時間，時間毫秒值")
	private Long stopTime;
    
	
    
	
}
