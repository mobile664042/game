package com.simple.game.core.domain.service;

import java.util.concurrent.ConcurrentHashMap;

import com.simple.game.core.domain.dto.OnlineInfo;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.good.BaseGame;

import lombok.Data;

/***
 * 
 * 摸拟抽象的游戏管理
 * 
 * @author zhibozhang
 *
 */
@Data
public class GameTestManager {
	/***
	 * key playerId
	 */
	private static final ConcurrentHashMap<Long, Player> online_player = new ConcurrentHashMap<Long, Player>();
	
	/***
	 * key gameCode
	 */
	private static final ConcurrentHashMap<Integer, BaseGame> online_game = new ConcurrentHashMap<Integer, BaseGame>(); 
	
	
	
	public void handlePlayer(Object o) {
		//TODO 
		online_player.get(null);
		
		Player player = new Player();
		
		OnlineInfo online = new OnlineInfo();
		player.setOnline(online);
		
		online_player.put(player.getId(), player);
	}
	
	
	public void handleAdmin(Object o) {
		//TODO 
		online_player.get(null);
		
		BaseGame game = null;
		
		
		online_game.put(game.getGameItem().getNo(), game);
		
		
	}
	
	
}
