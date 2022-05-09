package com.simple.game.core.domain.good;

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
import com.simple.game.core.domain.cmd.push.game.notify.PushSysChatCmd;
import com.simple.game.core.domain.cmd.rtn.game.RtnGameInfoCmd;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.config.GameItem;
import com.simple.game.core.domain.dto.constant.ChatKind;
import com.simple.game.core.domain.dto.constant.GameStatus;
import com.simple.game.core.domain.ext.Chat;
import com.simple.game.core.exception.BizException;
import com.simple.game.core.util.MyThreadFactory;
import com.simple.game.core.util.SimpleUtil;

import lombok.Getter;
import lombok.ToString;

/***
 * 
 * 最基本的游戏设计
 * 
 * 游戏中最基础的组件部分
 * 
 * @author zhibozhang
 *
 */
@Getter
@ToString
public abstract class BaseGame{
	public static final int PAGE_SIZE = 20;
	protected final static AtomicInteger INDEX = new AtomicInteger(100);
	
	private final static Logger logger = LoggerFactory.getLogger(BaseGame.class);
	private static final int coreSize = Runtime.getRuntime().availableProcessors();
	private final static ThreadPoolExecutor pool = new ThreadPoolExecutor(1, coreSize,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100000),
            new MyThreadFactory("broadcast"));
	
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
	
	
	private long lastLogTime = System.currentTimeMillis();
	
	/***游戏配置****/
	protected GameItem gameItem;
	
	/***游戏状态****/
	protected GameStatus gameStatus;
	
	/***暂停结束时间****/
	private long pauseEndTime;
	
	/***游戏初使化****/
	public BaseGame( GameItem gameItem){
		logger.info("游戏准备初使化");
		long startTime = System.currentTimeMillis();
		gameStatus = GameStatus.ready;
		this.gameItem = gameItem;
		logger.info("游戏初使化完成, 耗时:{}", (System.currentTimeMillis() - startTime));
	}
	
	/***游戏运行(每隔250毫秒中扫描一次，推动游戏一直运行)****/
	public final synchronized void scan() {
		if(gameStatus == null || gameStatus == GameStatus.finished) {
			return;
		}
		
		if(this.getPauseTime() > 0) {
			//游戏暂停中
			return ;
		}
		
		gameStatus = GameStatus.ready;
		long startTime = System.currentTimeMillis();
		try {
			boolean result = onScan();
			long time = System.currentTimeMillis() - startTime;
			if(result || (System.currentTimeMillis()-lastLogTime) > 600000 || time > 2000) {
				logger.info("游戏进行一次计算, 耗时:{}", time);
				lastLogTime = System.currentTimeMillis();
			}
		}
		catch(Exception e) {
			logger.error("游戏计算异场", e);
		}
	}
	/***游戏运行(每隔250毫秒中扫描一次，推动游戏一直运行)(需要打印日志时返回true或有变化时返回true)****/
	protected abstract boolean onScan();
	
	/***中止游戏,强制结算,强退所有人员，销毁游戏****/
	public final synchronized void destroy() {
		if(gameStatus == null) {
			throw new BizException("游戏还未初使化，不可销毁！！！");
		}
		if(gameStatus == GameStatus.finished) {
			return;
		}
		
		logger.info("游戏准备销毁");
		long startTime = System.currentTimeMillis();
		onDestroy();
		logger.info("游戏销毁完成, 耗时:{}", (System.currentTimeMillis() - startTime));
	}
	protected void onDestroy() {};
	
	/***游戏暂停多久时间(毫秒)(小于等于0表示游戏没有暂停)****/
	public final long getPauseTime() {
		if(pauseEndTime == 0) {
			return 0;
		}
		long time = pauseEndTime - System.currentTimeMillis();
		if(time < 0) {
			return 0;
		}
		return time;
	}
	/***操作验证***/
	protected final void operatorVerfy() {
		if(gameStatus == null) {
			throw new BizException("游戏还未初使化，不可进入！！！");
		}
		if(gameStatus == GameStatus.finished) {
			throw new BizException("游戏已准备销毁，不可进入！！！");
		}
	}
	
	/***
	 * 进入游戏
	 */
	public abstract RtnGameInfoCmd join(Player player);

	/***
	 * 获取游戏在线玩家
	 * 需要页面这个接口的访问频率
	 */
	public List<Player> getOnlineList(int fromPage) {
		List<Player> list = new ArrayList<Player>(playerMap.values());
		
		List<Player> result = new ArrayList<Player>();
		int fromIndex = fromPage * PAGE_SIZE;
		int toIndex = fromIndex + PAGE_SIZE;
		for(int i=fromIndex; i<list.size() && i<toIndex; i++) {
			result.add(list.get(i));
		}
		return result;
	}
	
	/***
	 * 发送游戏广播
	 * @param cmd
	 * @param excludeIds
	 */
	public final void broadcast(PushCmd cmd, long ...excludeIds){
		broadcast(cmd, true, excludeIds);
	}
	public void broadcast(final PushCmd cmd, boolean async, final long ...excludeIds){
		this.operatorVerfy();
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
	
	/***
	 * 离开游戏
	 */
	public final void left(long playerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		this.preLeft(playerId);
		
		Player player = playerMap.remove(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		offlineMap.remove(playerId);
		outParam.setParam(player);
		player.setAddress(null);
		
		logger.info("{}离开{}游戏", outParam.getParam().getNickname(), gameItem.getName());
	}
	protected void preLeft(long playerId) {}
	
	
	/***聊天****/
	public final void chat(long playerId, Chat message, OutParam<Player> outParam) {
		this.operatorVerfy();
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("非法请求，不在游戏桌中"));
		}
		outParam.setParam(player);
		logger.info("{}在游戏:{},发送:{}聊天", outParam.getParam().getNickname(), gameItem.getName(), message.getKind());
	}

	/***系统强制踢人***/
	public final void kickout(long playerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		this.preLeft(playerId);
		
		Player player = playerMap.remove(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		
		//发送给玩家
		PushSysChatCmd pushCmd = new PushSysChatCmd();
		Chat chat = new Chat();
		chat.setKind(ChatKind.text);
		chat.setContent("你被强制踢下线了！");
		pushCmd.setChat(chat);
		player.getOnline().push(pushCmd);
		
		offlineMap.remove(playerId);
		outParam.setParam(player);
		player.setAddress(null);
		
		logger.info("强制在游戏:{},将{}踢走", gameItem.getName(), outParam.getParam().getNickname());
	}
	
	/***游戏暂停****/
	public final void pause(int seconds) {
		this.operatorVerfy();
		pauseEndTime = System.currentTimeMillis() + seconds * 1000; 
		logger.info("游戏:{}准备暂停{}秒", gameItem.getName(), seconds);
	}
	/***游戏取消暂停(恢复正常)****/
	public final void resume() {
		this.operatorVerfy();
		pauseEndTime = 0; 
		logger.info("游戏:{}恢复正常", gameItem.getName());
	}
	
	/***断网，掉线***/
	public final void disconnect(long playerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		outParam.setParam(player);
		offlineMap.put(playerId, player);
		player.getOnline().setSession(null);
		
		logger.info("游戏:{}的{}玩家掉线", gameItem.getName(), outParam.getParam().getNickname());
	}
	
	/***掉线重连***/
	public final void connect(long playerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		outParam.setParam(player);
		offlineMap.remove(playerId);
		logger.info("游戏:{}的{}玩家掉线", gameItem.getName(), outParam.getParam().getNickname());
	}
	
	public int getOnlineCount() {
		return playerMap.size();
	}
}
