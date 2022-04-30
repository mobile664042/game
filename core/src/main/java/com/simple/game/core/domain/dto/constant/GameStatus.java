package com.simple.game.core.domain.dto.constant;

/****
 * 游戏状态
 * @author zhibozhang
 *
 */
public enum GameStatus {
	/***初使化完成之后****/
	ready,
	
	/***有人在游戏中****/
	running,
	
	/***游戏准备退出(已退出全部人员，不再允许人员进行, 准备从内存中销毁)****/
	finished,
}
