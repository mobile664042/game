package com.simple.game.core.domain.dto;

import java.io.IOException;
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

import com.simple.game.core.constant.GameConstant;
import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.game.PushConnectCmd;
import com.simple.game.core.domain.cmd.push.game.PushDisconnectCmd;
import com.simple.game.core.domain.cmd.push.game.PushLeftCmd;
import com.simple.game.core.domain.cmd.push.game.PushPauseCmd;
import com.simple.game.core.domain.cmd.push.game.PushResumeCmd;
import com.simple.game.core.domain.cmd.push.game.notify.PushSysChatCmd;
import com.simple.game.core.domain.cmd.req.game.ReqConnectCmd;
import com.simple.game.core.domain.cmd.req.game.ReqDisconnectCmd;
import com.simple.game.core.domain.cmd.req.game.ReqGetOnlineListCmd;
import com.simple.game.core.domain.cmd.req.game.ReqJoinCmd;
import com.simple.game.core.domain.cmd.req.game.ReqLeftCmd;
import com.simple.game.core.domain.cmd.rtn.game.RtnGetOnlineListCmd;
import com.simple.game.core.domain.cmd.vo.PlayerVo;
import com.simple.game.core.domain.dto.config.GameItem;
import com.simple.game.core.domain.dto.constant.ChatKind;
import com.simple.game.core.domain.dto.constant.GameStatus;
import com.simple.game.core.domain.ext.Chat;
import com.simple.game.core.exception.BizException;
import com.simple.game.core.util.GameSession;
import com.simple.game.core.util.MyThreadFactory;
import com.simple.game.core.util.SimpleUtil;

/***
 * 游戏桌包含游戏桌最基本的部分
 * 
 * @author zhibozhang
 *
 */
public abstract class BaseDesk{
	/****桌号位序***/
	protected final static AtomicInteger NUMBER_INDEX = new AtomicInteger(101); 
	protected final static Logger logger = LoggerFactory.getLogger(BaseDesk.class);
	
	public static final int PAGE_SIZE = 20;
	protected final static AtomicInteger INDEX = new AtomicInteger(100);
	
	protected static final int coreSize = Runtime.getRuntime().availableProcessors();
	protected final static ThreadPoolExecutor pool = new ThreadPoolExecutor(1, coreSize,
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
	
	
	protected long lastLogTime = System.currentTimeMillis();
	
	/***游戏配置****/
	protected GameItem gameItem;
	
	/***游戏状态****/
	protected GameStatus gameStatus;
	
	/***暂停结束时间****/
	protected long pauseEndTime;
	
	/***
	 * 桌号
	 */
	protected final int deskNo;
	
	
	public BaseDesk(GameItem gameItem) {
		logger.info("游戏桌准备初使化");
		long startTime = System.currentTimeMillis();
		this.gameItem = gameItem;
		deskNo = NUMBER_INDEX.getAndIncrement();
		gameStatus = GameStatus.ready;
		logger.info("游戏桌初使化完成, 耗时:{}", (System.currentTimeMillis() - startTime));
	} 

	public GameItem getGameItem() {
		return gameItem;
	}
	
	public GameStatus getGameStatus() {
		return gameStatus;
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
	/***游戏运行(每隔250毫秒中扫描一次，推动游戏一直运行)(需要打印日志时返回true或有变化时返回true)****/
	protected abstract boolean onScan();
	/***操作验证***/
	public final void operatorVerfy() {
		if(gameStatus == null) {
			throw new BizException("游戏还未初使化，不可进入！！！");
		}
		if(gameStatus == GameStatus.finished) {
			throw new BizException("游戏已准备销毁，不可进入！！！");
		}
	}
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
	
	public Player getPlayer(long playerId) {
		return playerMap.get(playerId);
	}
	public int getOnlineCount() {
		return playerMap.size();
	}
	public int getDeskNo() {
		return deskNo;
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
	 * 获取游戏在线玩家
	 * 需要页面这个接口的访问频率
	 */
	public void getOnlineList(GameSessionInfo gameSessionInfo, ReqGetOnlineListCmd reqCmd) {
		List<Player> list = new ArrayList<Player>(playerMap.values());
		int fromIndex = reqCmd.getFromPage() * PAGE_SIZE;
		int toIndex = fromIndex + PAGE_SIZE;
		
		List<PlayerVo> voList = new ArrayList<PlayerVo>(list.size());
		RtnGetOnlineListCmd rtnCmd = new RtnGetOnlineListCmd();
		rtnCmd.setList(voList);
		for(int i=fromIndex; i<list.size() && i<toIndex; i++) {
			Player player = list.get(i);
			PlayerVo vo = player.valueOfPlayerVo();
			voList.add(vo);
		}
		Player player = this.getPlayer(gameSessionInfo.getPlayerId());
		player.getOnline().getSession().write(rtnCmd);
	}
	/***
	 * 离开游戏
	 */
	public final void left(GameSessionInfo gameSessionInfo, ReqLeftCmd reqCmd) {
		long playerId = gameSessionInfo.getPlayerId();
		left(playerId);
	}
	protected void left(long playerId) {
		this.preLeft(playerId);
		
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		
		GameSessionInfo gameSessionInfo = (GameSessionInfo)player.getOnline().getSession().getAttachment().get(GameConstant.GAME_SESSION_INFO);
		if(gameSessionInfo.getAddress() instanceof GameSeat) {
			GameSeat gameSeat = (GameSeat)gameSessionInfo.getAddress();
			gameSeat.standUp(playerId);
		}
		offlineMap.remove(playerId);
		gameSessionInfo.setAddress(null);
		
		//广播离开信息
		PushLeftCmd pushCmd = new PushLeftCmd();
		pushCmd.setPlayerId(playerId);
		pushCmd.setNickname(player.getNickname());
		pushCmd.setHeadPic(player.getHeadPic());
		this.broadcast(pushCmd, gameSessionInfo.getPlayerId());
		logger.info("{}离开{}游戏", player.getNickname(), gameItem.getName());
	}
	protected void preLeft(long playerId) {}
	
	/***系统强制踢人***/
	public final void kickout(long playerId) {
		this.preLeft(playerId);
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		
		//发送给玩家
		{
			PushSysChatCmd pushCmd = new PushSysChatCmd();
			Chat chat = new Chat();
			chat.setKind(ChatKind.text);
			chat.setContent("你被强制踢下线了！");
			pushCmd.setChat(chat);
			player.getOnline().push(pushCmd);
		}
		
		//离开游戏
		left(playerId);
		
		try {
			player.getOnline().closeSession();
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
		
		logger.info("强制在游戏:{},将{}踢走", gameItem.getName(), player.getNickname());
	}
	
	
	/***断网，掉线***/
	public final void disconnect(GameSessionInfo gameSessionInfo, ReqDisconnectCmd reqCmd) {
		this.operatorVerfy();
		long playerId = gameSessionInfo.getPlayerId();
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		
		//广播离开信息
		PushDisconnectCmd pushCmd = reqCmd.valueOfPushDisconnectCmd();
		pushCmd.setPlayerId(playerId);
		pushCmd.setNickname(player.getNickname());
		pushCmd.setHeadPic(player.getHeadPic());
		this.broadcast(pushCmd, gameSessionInfo.getPlayerId());
		
		offlineMap.put(playerId, player);
		player.getOnline().setSession(null);
		logger.info("游戏:{}的{}玩家掉线", gameItem.getName(), player.getNickname());
	}
	
	/***掉线重连***/
	public final void connect(GameSession gameSession, ReqConnectCmd reqCmd) {
		GameSessionInfo gameSessionInfo = (GameSessionInfo)gameSession.getAttachment().get(GameConstant.GAME_SESSION_INFO);
		long playerId = gameSessionInfo.getPlayerId();
		
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		
		try {
			//挤下线
			PushSysChatCmd pushCmd = new PushSysChatCmd();
			Chat chat = new Chat();
			chat.setKind(ChatKind.text);
			chat.setContent("你被挤下线了！");
			pushCmd.setChat(chat);
			player.getOnline().push(pushCmd);
			logger.error("{}旧的在线被挤下去了", player.getId());
			//主动断线
			player.getOnline().closeSession();
		}
		catch(Exception e) {
			logger.error("{}主动断线失败", player.getId(), e);
		}
		
		offlineMap.remove(playerId);
		
		//重新更改session
		changeSession(player, gameSession);
		
		//广播重连信息
		PushConnectCmd pushCmd = reqCmd.valueOfPushConnectCmd();
		pushCmd.setPlayerId(playerId);
		pushCmd.setNickname(player.getNickname());
		pushCmd.setHeadPic(player.getHeadPic());
		this.broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}
	
	
	protected void changeSession(Player player, GameSession session) {
		OnlineInfo onlineInfo = player.getOnline();
		if(onlineInfo == null) {
			onlineInfo = new OnlineInfo();
			player.setOnline(onlineInfo);
		}
		onlineInfo.setLastIp(session.getRemoteAddr());
		onlineInfo.setLoginTime(System.currentTimeMillis());
		onlineInfo.setLoginIp(session.getRemoteAddr());
		onlineInfo.setSession(session);
	}
	/***
	 * 进入游戏
	 */
	public abstract void join(ReqJoinCmd reqCmd, GameSession gameSession);
	
	
	/***游戏暂停****/
	public final void pause(int seconds) {
		this.operatorVerfy();
		pauseEndTime = System.currentTimeMillis() + seconds * 1000; 
		logger.info("游戏:{}准备暂停{}秒", gameItem.getName(), seconds);
		
		PushPauseCmd pushCmd = new PushPauseCmd();
		pushCmd.setSeconds(seconds);
		this.broadcast(pushCmd);
	}
	/***游戏取消暂停(恢复正常)****/
	public final void resume() {
		this.operatorVerfy();
		pauseEndTime = 0; 
		logger.info("游戏:{}恢复正常", gameItem.getName());
		
		PushResumeCmd pushCmd = new PushResumeCmd();
		this.broadcast(pushCmd);
	}
	
	
}
