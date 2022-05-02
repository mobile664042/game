package com.simple.game.core;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.simple.game.core.domain.dto.config.DeskItem;
import com.simple.game.core.domain.dto.config.GameItem;
import com.simple.game.core.domain.good.BaseGame;

/***
 * 游戏基础包装组件
 * 
 * @author zhibozhang
 *
 */
public abstract class GameWrap {
	/***
	 * 游戏桌缓存
	 * key com.simple.game.core.domain.wrap.DeskConfigReq.kind 类型
	 */
	protected final ConcurrentHashMap<Integer, GameDeskWrap> gameDeskMap = new ConcurrentHashMap<Integer, GameDeskWrap>();
	
	public void load(GameItem gameConfigReq, List<DeskConfigReq> list) {
		for(DeskConfigReq deskConfigReq : list) {
			BaseGame baseGame = load(gameConfigReq, deskConfigReq.getDeskItem());
			GameDeskWrap gameDeskWrap = new GameDeskWrap(baseGame);
			gameDeskMap.put(deskConfigReq.getKind(), gameDeskWrap);
		}
	}

	public abstract BaseGame load(GameItem gameItem, DeskItem deskItem);
}
