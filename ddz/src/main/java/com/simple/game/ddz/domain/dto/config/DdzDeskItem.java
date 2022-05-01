package com.simple.game.ddz.domain.dto.config;

import com.simple.game.core.domain.dto.config.DeskItem;

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
	
	/***最小可进行一轮游戏的币****/
	private int minReadyCoin = 500;

	/***单价****/
	private int unitPrice = 5;
}
