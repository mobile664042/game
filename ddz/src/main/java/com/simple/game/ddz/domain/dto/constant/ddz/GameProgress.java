package com.simple.game.ddz.domain.dto.constant.ddz;

public enum GameProgress {
	/****准备阶段***/
	ready,
	
	/****已发完牌阶段，准备抢地主***/
	sended,
	
	/****已抢地主, 出牌阶段***/
	robbedLandlord,
	
	/*****/
	gameover,
	
	/**额外扩展的状态(效果与gameover类似)***/
	surrender,
}
