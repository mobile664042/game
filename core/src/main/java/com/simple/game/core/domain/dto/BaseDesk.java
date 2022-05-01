package com.simple.game.core.domain.dto;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.push.PushChatCmd;
import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.PushConnectedCmd;
import com.simple.game.core.domain.cmd.push.PushDisconnectCmd;
import com.simple.game.core.domain.cmd.push.PushLeftCmd;
import com.simple.game.core.domain.cmd.rtn.RtnGameInfoCmd;
import com.simple.game.core.domain.cmd.rtn.RtnOnlineListCmd;
import com.simple.game.core.domain.ext.Chat;
import com.simple.game.core.domain.good.BaseGame;
import com.simple.game.core.exception.BizException;
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
	private static Logger logger = LoggerFactory.getLogger(BaseDesk.class);
	
	private static ThreadPoolExecutor pool = new ThreadPoolExecutor(1,  Runtime.getRuntime().availableProcessors(),
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100000));
	
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
	} 
	public int getAddrNo() {
		return this.currentGame.getDeskItem().getNumber();
	}
	
	/***
	 * @param player
	 * @throws BizException 验证不符号进入条件时会抛异常
	 */
	public RtnGameInfoCmd join(Player player) throws BizException{
		//不需要加锁判断，减少时死锁，提高性能，允许极少量的误差
		if(playerMap.size()+1 > currentGame.getDeskItem().getMaxPersion()) {
			throw new BizException(String.format("人员已挤不下去了(已有%s)", currentGame.getDeskItem().getMaxPersion()));
		}
		playerMap.put(player.getId(), player);
		player.setAddress(this);
		return getRtnGameInfoCmd();
	}
	
	protected Player buildGamePlayer(Player player) {
		return player;
	}
	
	protected RtnGameInfoCmd getRtnGameInfoCmd() {
		return new RtnGameInfoCmd();
	}
	

	protected RtnGameInfoCmd getOnlineListCmd() {
		return new RtnGameInfoCmd();
	}
	
	/***
	 * 聊天
	 * @param playerId
	 * @param message
	 * @param outParam
	 * @return
	 */
	public PushChatCmd chat(long playerId, Chat message, OutParam<Player> outParam) {
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("非法请求，不在游戏桌中"));
		}
		outParam.setParam(player);
		//TODO 需判断扣款
		return player.toPushChatCmd(message);		
	}
	
	public void broadcast(PushCmd cmd, long ...excludeIds){
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
			pool.submit(task);
		}
		catch(Exception e) {
			String str = "";
			if(excludeIds != null && excludeIds.length > 0) {
				str = Arrays.toString(excludeIds);
			}
			logger.error("cmd={}, excludes={} 广播失败！", cmd.toLogStr(), str, e);
		}
	}

	public PushLeftCmd left(long playerId, OutParam<Player> out) {
		Player player = playerMap.remove(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		offlineMap.remove(playerId);
		out.setParam(player);
		player.setAddress(null);
		return player.toPushLeftCmd();
	}

	

	public PushDisconnectCmd disconnect(long playerId, OutParam<Player> outParam) {
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		outParam.setParam(player);
		offlineMap.put(playerId, player);
		player.getOnline().setSession(null);
		
		return player.toPushDisconnectCmd();
	}
	public PushConnectedCmd connected(long playerId, OutParam<Player> outParam) {
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		outParam.setParam(player);
		offlineMap.remove(playerId);
		//TODO
		player.getOnline().setSession(null);
		return player.toPushConnectedCmd();
	}
	public RtnOnlineListCmd getRtnOnlineListCmd() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
