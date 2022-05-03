package com.simple.game.server.cmd.req.user;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("登录")
public class LoginReq {
	@NotEmpty
	@Length(min=2, max=20)
	@ApiModelProperty(value = "用户", required = true)
    private String username;
	
	@NotEmpty
	@Length(min=2, max=20)
	@ApiModelProperty(value = "密码", required = true)
	private String password;

}
