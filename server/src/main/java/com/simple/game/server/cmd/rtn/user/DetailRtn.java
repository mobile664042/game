package com.simple.game.server.cmd.rtn.user;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.simple.game.server.dbEntity.User;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户详情信息")
public class DetailRtn {
	
	@ApiModelProperty(value = "用户在线gameCode", required = true)
    private List<String> gameCodes;
    
	@ApiModelProperty(value = "用户基本信息", required = true)
    private EntityRtn entityRtn;
    
    public static DetailRtn valueOfUser(User user) {
    	DetailRtn o = new DetailRtn();
    	BeanUtils.copyProperties(user, o);
    	return o;
    }
}
