package com.simple.game.core.domain.dto;

/***
 * 
 * 地址编号
 * 
 * 在游戏中的地址是分层级：>游戏>区>场>房>桌>席位(暂时只有玩法、桌、席位三层地址)
 * 例如：@45@78@1
 * 
 * 
 * 
 * 在游戏中的地址
 * 
 * @author zhibozhang
 *
 */
public interface AddressNo {
	/***
	 * 
	 * 地址编号
	 * 玩法、桌、席位三层地址
	 * @45@78@1
	 * ***/
	String getAddrNo();
}
