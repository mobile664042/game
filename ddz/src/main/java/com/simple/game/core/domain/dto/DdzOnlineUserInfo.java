package com.simple.game.core.domain.dto;

import lombok.Data;
import lombok.ToString;

/***
 * 在线信息
 * 
 * @author zhibozhang
 *
 */
@Data
@ToString
public class DdzOnlineUserInfo extends OnlineUserInfo{
	/**游戏等级***/
	private int gameLevel;
	/**当前经验值***/
	private int expValue;
	/**vip等级***/
	private int vipLevel;
	
	public DdzOnlineUserInfo(OnlineUserInfo onlineUserInfo) {
		//TODO 
	}
}
