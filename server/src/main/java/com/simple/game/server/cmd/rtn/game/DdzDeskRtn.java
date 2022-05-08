package com.simple.game.server.cmd.rtn.game;

import com.simple.game.core.domain.dto.constant.GameStatus;
import com.simple.game.ddz.domain.dto.constant.ddz.GameProgress;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("游戏桌")
public class DdzDeskRtn {
	@ApiModelProperty(value = "桌号", required = true)
	private int deskNo;
	
	@ApiModelProperty(value = "在线人数")
	private int onlineCount = 0;
	
	@ApiModelProperty(value = "游戏状态", required = true)
	private GameStatus gameStatus;
	
	@ApiModelProperty(value = "游戏进度", required = true)
	private GameProgress currentProgress;
}
