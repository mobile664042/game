package com.simple.game.core.domain.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.push.game.notify.PushDestroyCmd;
import com.simple.game.core.domain.dto.TableDesk;
import com.simple.game.core.domain.dto.config.DeskItem;
import com.simple.game.core.domain.dto.config.GameItem;
import com.simple.game.core.exception.BizException;
import com.simple.game.core.util.MyThreadFactory;

/***
 * 抽象的游戏管理
 * 
 * @author zhibozhang
 *
 */
public abstract class GameManager {
	private final static Logger logger = LoggerFactory.getLogger(GameManager.class);
	/****
	 * 扫描的间隔间长
	 */
	private static final int INTERVAL_MS  = 250;
	
	private static final int coreSize = Runtime.getRuntime().availableProcessors();
	private final static ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(coreSize, new MyThreadFactory("gameScan"));
	
	/***
	 * 游戏桌缓存
	 * 
	 * key com.simple.game.core.domain.dto.config.DeskItem.playKind 类型
	 * 
	 * value.key com.simple.game.core.domain.dto.BaseDesk.number 桌号
	 * 
	 */
	protected final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, TableDesk>> gameDeskMap = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, TableDesk>>();
	
	
	protected GameItem gameItem;
	protected HashMap<Integer, DeskItem> deskItemMap = new HashMap<Integer, DeskItem>();

	public void init() {
		gameItem = getGameItem();
		List<DeskItem> deskItemLst = getDeskItemList();
		for(DeskItem deskItem : deskItemLst) {
			deskItemMap.put(deskItem.getPlayKind(), deskItem);
		}
		boot();
	}
	
	public abstract GameItem getGameItem();
	public abstract List<DeskItem> getDeskItemList();
	public abstract TableDesk newInstanceload(GameItem gameItem, DeskItem deskItem);
	
	
	public void buildGameDesk(int kind, int count) {
		DeskItem item = deskItemMap.get(kind);
		if(item == null) {
			throw new BizException(String.format("没有%s类型的桌子", kind));
		}

		long startTime = System.currentTimeMillis();
		for(int i=0; i<count; i++) {
			TableDesk tableDesk = newInstanceload(gameItem, item);
			ConcurrentHashMap<Integer, TableDesk> deskMap = gameDeskMap.get(kind);
			if(deskMap == null) {
				deskMap = new ConcurrentHashMap<Integer, TableDesk>(); 
				gameDeskMap.put(kind, deskMap);
			}
			deskMap.put(tableDesk.getDeskNo(), tableDesk);
			logger.info("创建kind={}, 游戏桌编号={}完成", kind, tableDesk.getDeskNo());
		}
		
		logger.info("创建kind={}, 总量={}完成, 耗时:{}", kind, count, (System.currentTimeMillis() - startTime));
	}
	
	public void destroyGameDesk(int playKind, int deskNo) {
		ConcurrentHashMap<Integer, TableDesk> deskMap = gameDeskMap.get(playKind);
		if(deskMap == null) {
			throw new BizException(String.format("没有%s类型的桌子", playKind));
		}
		
		TableDesk tableDesk = deskMap.get(deskNo);
		if(tableDesk == null) {
			throw new BizException(String.format("没有%s类型的桌子编号为%s桌子", playKind, deskNo));
		}
		
		PushDestroyCmd pushCmd = new PushDestroyCmd();
//		pushCmd.setPlayKind(playKind);
//		pushCmd.setDeskNo(deskNo);
		tableDesk.broadcast(pushCmd, false);
		
		tableDesk.destroy();
		deskMap.remove(deskNo);
		if(deskMap.size() == 0) {
			gameDeskMap.remove(playKind);
		}
		logger.info("已销毁{}类型桌子编号为{}的游戏桌", playKind, tableDesk.getDeskNo());
	}
	
	/***游戏运行(每隔250毫秒中扫描一次，推动游戏一直运行)****/
	private void boot() {
		Runnable task = new Runnable() {
			public void run() {
				for(ConcurrentHashMap<Integer, TableDesk> deskMap : gameDeskMap.values()) {
					for(TableDesk baseGame : deskMap.values()) {
						try {
							baseGame.scan();
						}
						catch(Exception e) {
							logger.warn("游戏运行异常", e);
						}
					}
				}
			}
		};
		pool.scheduleAtFixedRate(task, INTERVAL_MS, INTERVAL_MS, TimeUnit.MILLISECONDS);
	}
	
	public TableDesk getTableDesk(int playKind, int deskNo) {
		ConcurrentHashMap<Integer, TableDesk> deskMap = gameDeskMap.get(playKind);
		if(deskMap == null) {
			return null;
		}
		
		return deskMap.get(deskNo);
	}
	
	public List<TableDesk> getTableDeskList(int playKind){
		ConcurrentHashMap<Integer, TableDesk> deskMap = gameDeskMap.get(playKind);
		if(deskMap == null) {
			return null;
		}
		
		return new ArrayList<TableDesk>(deskMap.values());
	}
}
