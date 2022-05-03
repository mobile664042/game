package com.simple.game.server.cmd.req.desk;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("系统恢复游戏桌")
public class ResumeReq {
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
}
