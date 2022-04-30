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
public class OnlineUserInfo {
	private long id;
	/**名称***/
	private String nickname;
	
	/**头像***/
	private String headPic;
	
	/**带入币(桌面币，不一定等于bankCoin, 一般使用这个做游戏计算，变动次数多)***/
	private long bcoin;
	
	/**游戏地址(通过它可以快速的找到玩家)***/
	private AddressNo address;
	
	/**在线信息***/
	private OnlineInfo online;
	
}
