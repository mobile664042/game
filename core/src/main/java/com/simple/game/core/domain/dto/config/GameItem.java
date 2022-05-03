package com.simple.game.core.domain.dto.config;

import lombok.Data;
import lombok.ToString;

/***
 * 游戏
 * 
 * @author zhibozhang
 *
 */
@Data
@ToString
public class GameItem {
	/***游戏名称(暂时只有斗地主)****/
	private String name = "斗地主";
	
	/***游戏内部编码(斗地主编码101010)(编规则第一二位表示扑克类，第三四位表是斗地主类，第五六位表示普通玩法)****/
	private int no = 101010;
	
	/***游戏封面****/
	private String indexPic;
	
	/***席位中最多助理个数****/
	private int seatMaxAssistant = 5;
	

	/***席位中最多粉丝个数****/
	private int seatMaxFans = 300;
	
	/***介绍****/
	private String desc;
}
