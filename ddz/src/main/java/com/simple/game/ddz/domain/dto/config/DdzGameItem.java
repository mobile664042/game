package com.simple.game.ddz.domain.dto.config;

import com.simple.game.core.domain.dto.config.GameItem;
import com.simple.game.ddz.domain.dto.constant.ddz.DoubleKind;

import lombok.Data;

/***
 * 游戏的扩展属性
 * 
 * @author zhibozhang
 *
 */
@Data
public class DdzGameItem extends GameItem{

	/***游戏加倍玩法****/
	private DoubleKind doubleKind = DoubleKind.exponential;

	/***最长等待抢地主时长****/
	private int maxRobbedLandlordSecond = 16;
	

	/***最长等待下一轮时长****/
	private int maxReadyNextSecond = 20;
	
	/***最长等待过牌时长(如果此时是自己出牌，自动选择最少的一张牌)****/
	private int maxPlayCardSecond = 12;
	
	/***一局游戏允许最大超时次数****/
	private int maxPlayCardOuttimeCount = 2;
	
	/***一局游戏允许最大跳过次数(断线后或超时跳过牌)****/
	private int maxSkipCount = 3;
	
	/***最长等待(主席位)掉线重连时长****/
	private int maxMasterDisconnectSecond = 40;
	
	/***最长等待(非主席位)掉线重连时长****/
	private int maxDisconnectSecond = 15;
	
	/***掉线时，等待重连后出时长****/
	private int disconnectPlayCardSecond = 1;
	
	
	/***跳过次数大于5将会收到逃跑处罚(处罚规则是,系统、每个席位赔一份农民输赢市)****/
	private int escape2SkipCount = 5;
	
	/***认输惩罚翻倍指数(快速结束游，队友不用赔钱)****/
	private int punishSurrenderDoubleCount = 2;
}
