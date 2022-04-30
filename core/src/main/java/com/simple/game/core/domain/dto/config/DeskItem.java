package com.simple.game.core.domain.dto.config;

import lombok.Data;

/***
 * 桌子
 * 
 * @author zhibozhang
 *
 */
@Data
public class DeskItem {
	/***游戏桌号从1号开始****/
	private int number;
	
	/***最多人员个数****/
	private int maxPersion = 1000;
	
	/***最小席位号(席位号从1开始)****/
	private int minPosition = 1;
	
	/***最大席位号(席位号从1开始)****/
	private int maxPosition = 3;
	
//	/***扩展属性****/
//	private Object extConfig;
	
}
