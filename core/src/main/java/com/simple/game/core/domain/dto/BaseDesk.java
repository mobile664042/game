package com.simple.game.core.domain.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.ext.Chat;
import com.simple.game.core.domain.good.BaseGame;
import com.simple.game.core.exception.BizException;
import com.simple.game.core.util.MyThreadFactory;
import com.simple.game.core.util.SimpleUtil;

import lombok.Getter;
import lombok.ToString;

/***
 * 游戏桌
 * 
 * 与游戏本身最基础的
 * 
 * 检测失败抛异常
 * 
 * @author zhibozhang
 *
 */
//@Data
@Getter
@ToString
public class BaseDesk implements AddressNo{
	private final static Logger logger = LoggerFactory.getLogger(BaseDesk.class);
	private static final int coreSize = Runtime.getRuntime().availableProcessors();
	private final static ThreadPoolExecutor pool = new ThreadPoolExecutor(1, coreSize,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100000),
            new MyThreadFactory("broadcast"));
	
	/****桌号位序***/
	private final static AtomicInteger NUMBER_INDEX = new AtomicInteger(101); 
	
	/***
	 * 桌号
	 */
	private final int number;
	
	/***
	 * 进入的玩家
	 * 会有很多旁观人群
	 * key playerId
	 */
	protected final ConcurrentHashMap<Long, Player> playerMap = new ConcurrentHashMap<Long, Player>();
	
	/***
	 * 掉线用户 
	 * 减少遍历playerMap
	 * key playerId
	 */
	protected final ConcurrentHashMap<Long, Player> offlineMap = new ConcurrentHashMap<Long, Player>();
	
	
	/***当前正在进行的游戏****/
	protected BaseGame currentGame;

	public BaseDesk(BaseGame game) {
		this.currentGame = game;
		number = NUMBER_INDEX.getAndIncrement();
	} 
	public int getAddrNo() {
		return number;
	}
	
	/***
	 * @param player
	 * @throws BizException 验证不符号进入条件时会抛异常
	 */
	public void join(Player player) throws BizException{
		//不需要加锁判断，减少时死锁，提高性能，允许极少量的误差
		if(playerMap.size()+1 > currentGame.getDeskItem().getMaxPersion()) {
			throw new BizException(String.format("人员已挤不下去了(已有%s)", currentGame.getDeskItem().getMaxPersion()));
		}
		playerMap.put(player.getId(), player);
		player.setAddress(this);
	}

	/***
	 * 聊天
	 * @param playerId
	 * @param message
	 * @param outParam
	 * @return
	 */
	public void chat(long playerId, Chat message, OutParam<Player> outParam) {
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("非法请求，不在游戏桌中"));
		}
		outParam.setParam(player);
		//TODO 需判断扣款
	}
	
	public void broadcast(PushCmd cmd, boolean async, long ...excludeIds){
		logger.info("cmd={}, 接收到推送信息！", cmd.toLogStr());
		Runnable task = new Runnable() {
			@Override
			public void run() {
				synchronized (this) {
					long startTime = System.currentTimeMillis();
					for(Player player : playerMap.values()) {
						if(SimpleUtil.contain(player.getId(), excludeIds)) {
							continue;
						}
						player.getOnline().push(cmd);
					}
					logger.info("cmd={}, 推送{}个人完成, 耗时:{}毫秒！", cmd.toLogStr(), playerMap.size(), System.currentTimeMillis() - startTime);
				}
			}
		};
		try {
			if(async) {
				pool.submit(task);
			}
			else {
				task.run();
			}
		}
		catch(Exception e) {
			String str = "";
			if(excludeIds != null && excludeIds.length > 0) {
				str = Arrays.toString(excludeIds);
			}
			logger.error("cmd={}, excludes={} 广播失败！", cmd.toLogStr(), str, e);
		}
	}

	public void left(long playerId, OutParam<Player> out) {
		Player player = playerMap.remove(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		offlineMap.remove(playerId);
		out.setParam(player);
		player.setAddress(null);
	}
	
	public void disconnect(long playerId, OutParam<Player> outParam) {
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		outParam.setParam(player);
		offlineMap.put(playerId, player);
		player.getOnline().setSession(null);
	}
	public void connect(long playerId, OutParam<Player> outParam) {
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		outParam.setParam(player);
		offlineMap.remove(playerId);
	}
	
	public List<Player> getOnlineList() {
		return new ArrayList<Player>(playerMap.values());
	}
	
	
}
