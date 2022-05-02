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
	/***玩法类型(唯一标识，用来确定其他字段)**/
	private int playKind;
	
	/***最多人员个数****/
	private int maxPersion = 1000;
	
	/***最小席位号(席位号从1开始)****/
	private int minPosition = 1;
	
	/***最大席位号(席位号从1开始)****/
	private int maxPosition = 3;
	
//	/***扩展属性****/
//	private Object extConfig;
	
}
