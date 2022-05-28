package com.simple.game.core.domain.dto;

import lombok.Data;

/***
 * 在线信息
 * 
 * @author zhibozhang
 *
 */
@Data
//@ToString
public class GameSessionInfo {
	private long playerId;
	
	/**游戏地址(通过它可以快速的找到玩家)***/
	private AddressNo address;
}
