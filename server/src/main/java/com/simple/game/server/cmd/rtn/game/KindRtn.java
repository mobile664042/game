package com.simple.game.server.cmd.rtn.game;

import org.springframework.beans.BeanUtils;

import com.simple.game.core.domain.dto.config.DeskItem;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("玩法信息")
public class KindRtn {
	@ApiModelProperty(value = "id", required = true)
	private Long id;
	
	@ApiModelProperty(value = "玩法类型", required = true)
	private int playKind;
	
	@ApiModelProperty(value = "一张桌子最多人员个数", required = true)
	private int maxPersion = 1000;
	
	@ApiModelProperty(value = "最小席位号", required = true)
	private int minPosition = 1;
	
	@ApiModelProperty(value = "最大席位号", required = true)
	private int maxPosition = 3;
	
	/***介绍****/
	@ApiModelProperty(value = "介绍")
	private String desc;

	public static KindRtn valueOfUser(DeskItem deskItem) {
		KindRtn o = new KindRtn();
    	BeanUtils.copyProperties(deskItem, o);
    	return o;
    }
}
