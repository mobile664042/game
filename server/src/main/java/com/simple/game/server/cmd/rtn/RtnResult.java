package com.simple.game.server.cmd.rtn;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 封装返回结果集
 *
 */
@Data
@ApiModel("返回结果集")
public class RtnResult<T> {
    /**
     * 返回状态码(0表示成功，其他的都可能是失败)
     * 非0时, 其他字段都可能为空
     */
	@ApiModelProperty("返回状态码(0表示成功，其他的都可能是失败)")
    private int code;

	@ApiModelProperty("返回的数据")
    private T data;

	@ApiModelProperty("总条数(如果是分页查找, 其他情况都是空)")
    private Long total;

	@ApiModelProperty("消息提示")
    private String msg;
	
	public static RtnResult<?> failure(String errDesc) {
		RtnResult<Object> rtn = new RtnResult<>();
        rtn.msg = errDesc;
        rtn.setCode(500);
        return rtn;
    }
	
	public static <T> RtnResult<T> success(T data) {
		RtnResult<T> rtn = new RtnResult<T>();
        rtn.data = data;
        return rtn;
    }
	
	public static RtnResult<?> invalidSession() {
		RtnResult<Object> rtn = new RtnResult<>();
		rtn.msg = "你失效了，请重新登录吧";
        rtn.setCode(302);
        return rtn;
    }
}