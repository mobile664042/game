package com.simple.game.core.domain.dto.config;

import lombok.Data;

/***
 * 桌子的扩展属性
 * 
 * @author zhibozhang
 *
 */
@Data
public class DdzDeskItem extends DeskItem{

	/***最小坐下币****/
	private int minSitdownCoin = 1000;
	
	/***最小强制站起币****/
	private int minStandupCoin = 500;

	/***单价****/
	private int unitPrice = 5;
}
