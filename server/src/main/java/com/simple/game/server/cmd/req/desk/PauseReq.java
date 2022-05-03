package com.simple.game.server.cmd.req.desk;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("系统暂停游戏桌")
public class PauseReq {
	@NotEmpty
	@Length(min=2, max=20)
	@ApiModelProperty(value = "在哪个游中")
    private String gameCode;
    
	@NotNull
	@Min(1)
	@ApiModelProperty(value = "哪种玩法")
    private Integer playKind;
	
	@NotNull
	@Min(1)
	@ApiModelProperty(value = "哪个游戏桌")
	private Integer deskNo;
	
	@NotNull
	@Min(1)
	@Max(24*3600)
	@ApiModelProperty(value = "暂停时长")
	private int second;
}
