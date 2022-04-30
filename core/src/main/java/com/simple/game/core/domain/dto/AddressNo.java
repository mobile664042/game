package com.simple.game.core.domain.dto;

/***
 * 
 * 地址编号
 * 
 * 在游戏中的地址是分层级：游戏>区>场>房>桌>席位(暂时只有桌与席位两层地址)
 * 
 * 
 * 在游戏中的地址
 * 
 * @author zhibozhang
 *
 */
public interface AddressNo {
	/***地下编号***/
	int getAddrNo();
}
