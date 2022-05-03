package com.simple.game.server.cmd.req.user;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;

import com.simple.game.server.dbEntity.User;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("注册用户")
public class AddReq {
	
	@NotEmpty
	@Length(min=2, max=20)
	@ApiModelProperty(value = "昵称", required = true)
    private String nickname;
    
	@NotEmpty
	@Length(min=2, max=20)
	@ApiModelProperty(value = "用户", required = true)
    private String username;
	
	@NotEmpty
	@Length(min=2, max=20)
	@ApiModelProperty(value = "密码", required = true)
	private String password;

    @NotNull
	@Min(1)
	@Min(2)
	@ApiModelProperty(value = "性别: 1男 2女", required = true)
    private Integer sex;
    
    @NotEmpty
	@Length(min=5, max=20)
	@ApiModelProperty(value = "手机号码", required = true)
    private String telphone;
    
    @NotNull
	@Min(1)
	@Min(200)
	@ApiModelProperty(value = "头像索引", required = true)
    private int headPic;
    
    public User valueOfUser() {
    	User user = new User();
    	BeanUtils.copyProperties(this, user);
    	return user;
    }
}
