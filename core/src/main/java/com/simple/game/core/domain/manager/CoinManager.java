package com.simple.game.core.domain.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.dto.Player;


/***
 * 
 * 记录游戏币的变化
 * 
 * @author zhibozhang
 *
 */
public class CoinManager {
	private static Logger logger = LoggerFactory.getLogger(CoinManager.class);
	
	public static void changeCoin(Player player, long coin, String batchNo, String reason) {
		long before = player.getBcoin();
		long after = player.addCoin(coin);
		logger.info("playerId={}, nickname={} 游戏币产生变化, {} + {} = {}, batchNo:{}, 变化原因:{} ", 
				player.getId(), player.getNickname(), before, coin, after, batchNo, reason);
	}
	
	public static void changeCoin(long playerId, long coin, String batchNo, String reason) {
		logger.info("playerId={}, 游戏币产生变化, 变动:{}  batchNo:{}, 变化原因:{} ", playerId, coin, batchNo, reason);
	}
	

	public static void logCoin(long playerId, long coin, long after, String batchNo, String reason) {
		logger.info("playerId={}, 游戏币产生变化, 变动:{}   变化后:{}, batchNo:{}, 变化原因:{} ", playerId, coin, after, batchNo, reason);
	}
}
