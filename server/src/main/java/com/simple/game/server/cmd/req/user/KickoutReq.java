package com.simple.game.server.cmd.req.user;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("踢玩家下线用户")
public class KickoutReq {
    @NotNull
	@Min(1)
	@ApiModelProperty(value = "用户id", required = true)
    private long playerId;
	
	@ApiModelProperty(value = "在哪个游中")
    private String gameCode;
    
	@ApiModelProperty(value = "哪种玩法")
    private Integer playKind;
	
	@ApiModelProperty(value = "哪个游戏桌")
	private Integer deskNo;
}
