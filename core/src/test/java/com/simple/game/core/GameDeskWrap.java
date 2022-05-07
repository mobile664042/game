package com.simple.game.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.good.BaseGame;
import com.simple.game.core.util.MyThreadFactory;

import lombok.ToString;

/***
 * 游戏桌基础包装组件
 * 
 * 游戏中最基础的组件部分
 * 
 * @author zhibozhang
 *
 */
@ToString
public class GameDeskWrap{
	/****
	 * 扫描的间隔间长
	 */
	private static final int INTERVAL_MS  = 250;
	
	private final static Logger logger = LoggerFactory.getLogger(GameDeskWrap.class);
	private static final int coreSize = Runtime.getRuntime().availableProcessors();
	private final static ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(coreSize, new MyThreadFactory("gameScan"));
	
	/***
	 * 游戏桌缓存
	 * key com.simple.game.core.domain.dto.BaseDesk.deskNo 桌号
	 */
	protected final ConcurrentHashMap<Integer, BaseGame> gameDeskMap = new ConcurrentHashMap<Integer, BaseGame>();
	
	private final BaseGame baseGame;
	
	GameDeskWrap(BaseGame baseGame) {
		this.baseGame = baseGame;
		boot();
	}
	
	/***游戏初使化****/
	void buildGameDesk(int count){
//		gameDeskMap.put(baseGame.getDeskNo(), baseGame);
//		logger.info("创建{}张游戏桌完成:{}", count, baseGame.getDeskNo());
	}
	
	/***游戏运行(每隔250毫秒中扫描一次，推动游戏一直运行)****/
	private void boot() {
		Runnable task = new Runnable() {
			public void run() {
				for(BaseGame baseGame : gameDeskMap.values()) {
					try {
						baseGame.scan();
					}
					catch(Exception e) {
						logger.warn("游戏运行异常", e);
					}
				}
			}
		};
		pool.scheduleAtFixedRate(task, INTERVAL_MS, INTERVAL_MS, TimeUnit.MILLISECONDS);
	}
}
