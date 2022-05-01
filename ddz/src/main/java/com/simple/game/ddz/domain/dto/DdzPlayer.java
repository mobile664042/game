package com.simple.game.ddz.domain.dto;

import com.simple.game.core.domain.dto.Player;

import lombok.Data;
import lombok.ToString;

/***
 * 游戏玩家
 * 
 * @author zhibozhang
 *
 */
@Data
@ToString
public class DdzPlayer extends Player{
	/**游戏等级***/
	private int gameLevel;
	/**当前经验值***/
	private int expValue;
	/**vip等级***/
	private int vipLevel;
	
	public DdzPlayer(Player player) {
		//TODO ;
	}
}
