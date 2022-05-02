package com.simple.game.core.domain.cmd.vo;

import lombok.Data;

@Data
public class PlayerVo {
	private long id;
	/**名称***/
	private String nickname;
	/**游戏等级***/
	private int gameLevel;
	/**当前经验值***/
	private int expValue;
	/**vip等级***/
	private int vipLevel;
	
	/**头像***/
	private String headPic;
}
