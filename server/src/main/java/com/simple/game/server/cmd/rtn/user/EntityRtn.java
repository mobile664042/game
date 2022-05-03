package com.simple.game.server.cmd.rtn.user;

import org.springframework.beans.BeanUtils;

import com.simple.game.server.dbEntity.User;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户信息")
public class EntityRtn {
	
	@ApiModelProperty(value = "id", required = true)
	private Long id;
	
	@ApiModelProperty(value = "昵称", required = true)
    private String nickname;
    
	@ApiModelProperty(value = "用户", required = true)
    private String username;

	@ApiModelProperty(value = "性别: 1男 2女", required = true)
    private Integer sex;
    
	@ApiModelProperty(value = "手机号码", required = true)
    private String telphone;
    
	@ApiModelProperty(value = "头像索引", required = true)
    private int headPic;
    
    public static EntityRtn valueOfUser(User user) {
    	EntityRtn o = new EntityRtn();
    	BeanUtils.copyProperties(user, o);
    	return o;
    }
}
