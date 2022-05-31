package com.simple.game.core.domain.dto;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.constant.GameConstant;
import com.simple.game.core.domain.cmd.push.game.PushApplyManagerCmd;
import com.simple.game.core.domain.cmd.push.game.PushChangeManagerCmd;
import com.simple.game.core.domain.cmd.push.game.PushJoinCmd;
import com.simple.game.core.domain.cmd.push.game.notify.PushNotifyApplyManagerCmd;
import com.simple.game.core.domain.cmd.push.seat.PushApproveBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.req.game.ReqApplyManagerCmd;
import com.simple.game.core.domain.cmd.req.game.ReqChangeManagerCmd;
import com.simple.game.core.domain.cmd.req.game.ReqJoinCmd;
import com.simple.game.core.domain.cmd.req.game.ReqKickoutCmd;
import com.simple.game.core.domain.cmd.req.game.ReqPauseCmd;
import com.simple.game.core.domain.cmd.req.game.ReqResumeCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApproveBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.rtn.game.RtnGameInfoCmd;
import com.simple.game.core.domain.cmd.vo.PlayerVo;
import com.simple.game.core.domain.dto.config.DeskItem;
import com.simple.game.core.domain.dto.config.GameItem;
import com.simple.game.core.exception.BizException;
import com.simple.game.core.util.GameSession;

/***
 * 游戏桌
 * 
 * 游戏附加的一些核心玩法
 * 
 * @author zhibozhang
 *
 */
public abstract class TableDesk extends BaseDesk implements AddressNo{
	private final static Logger logger = LoggerFactory.getLogger(TableDesk.class);
	/****管理员***/
	protected final AtomicReference<Player> manager = new AtomicReference<Player>();
	
	
	protected DeskItem deskItem;
	
	/***
	 * 正在进行席位
	 * key position,席位
	 */
	protected final ConcurrentHashMap<Integer, GameSeat> seatPlayingMap = new ConcurrentHashMap<Integer, GameSeat>();
	
	/***
	 * 支持的插件
	 */
	protected final ConcurrentHashMap<String, DeskPlugin> pluginMap = new ConcurrentHashMap<String, DeskPlugin>();
	
	
	public TableDesk(GameItem gameItem, DeskItem deskItem) {
		super(gameItem);
		this.deskItem = deskItem;
		for(int position = deskItem.getMinPosition(); position <= deskItem.getMaxPosition(); position++) {
			GameSeat gameSeat = buildGameSeat(position); 
			seatPlayingMap.put(position, gameSeat);
		}
		logger.info("游戏桌加载{}席位, 耗时:{}", (deskItem.getMaxPosition() - deskItem.getMinPosition() + 1));
	} 

	public String getAddrNo() {
		return "@" + deskItem.getPlayKind()  + "@" +  deskNo;
	}
	
	public DeskItem getDeskItem() {
		return deskItem;
	}
	
	void putPlugin(DeskPlugin plugin) {
		pluginMap.put(plugin.getPluginName(), plugin);
	}
	
	public DeskPlugin getPlugin(String pluginName) {
		return pluginMap.get(pluginName);
	}
	public final void kickout(GameSessionInfo gameSessionInfo, ReqKickoutCmd reqCmd) {
		checkAndGetManager(gameSessionInfo.getPlayerId());
		left(reqCmd.getOtherId());
	}

	protected void preJoin(Player player) {
		//不需要加锁判断，减少时死锁，提高性能，允许极少量的误差
		if(playerMap.size()+1 > deskItem.getMaxPersion()) {
			throw new BizException(String.format("人员已挤不下去了(已有%s)", deskItem.getMaxPersion()));
		}
	}
	/***
	 * 进入游戏
	 */
	public void join(ReqJoinCmd reqCmd, GameSession gameSession) {
		this.operatorVerfy();
		GameSessionInfo gameSessionInfo = (GameSessionInfo)gameSession.getAttachment().get(GameConstant.GAME_SESSION_INFO);
		if(gameSessionInfo.getAddress() != null) {
			rejoin(gameSession);
			return;
		}
		
		String nickName = (String)gameSession.getAttachment().get(GameConstant.NICKNAME);
    	int sex = (Integer)gameSession.getAttachment().get(GameConstant.SEX);
    	String telphone = (String)gameSession.getAttachment().get(GameConstant.TELPHONE);
    	int headPic = (Integer)gameSession.getAttachment().get(GameConstant.HEADPIC);
		
		Player player = new Player();
		player.setId(gameSessionInfo.getPlayerId());
		player.setNickname(nickName);
		player.setSex(sex);
		player.setTelphone(telphone);
		player.setHeadPic(String.valueOf(headPic));
		player.setBcoin(reqCmd.getBcoin());
		
		//是否有进入的限制条件
		this.preJoin(player);
		
		playerMap.put(player.getId(), player);
		gameSessionInfo.setAddress(this);
		changeSession(player, gameSession);
//		player.getOnline().getSession().getAttachment().put(GameConstant.GAME_SESSION_INFO, gameSessionInfo);
		
		RtnGameInfoCmd rtnCmd = getGameInfo();
		rtnCmd.setPlayerId(player.getId());
		logger.info("{}进入{}游戏, 返回:{} ", player.getNickname(), gameItem.getName(), rtnCmd.toLogStr());
		
		//发送广播
		PushJoinCmd pushCmd = reqCmd.valueOfPushJoinCmd();
		pushCmd.setPlayer(player.valueOfPlayerVo());
		this.broadcast(pushCmd, gameSessionInfo.getPlayerId());
		
		//把结果写回给用户
		gameSession.write(rtnCmd);
	}
	protected void rejoin(GameSession gameSession) {
		GameSessionInfo gameSessionInfo = (GameSessionInfo)gameSession.getAttachment().get(GameConstant.GAME_SESSION_INFO);
		Player player = getPlayer(gameSessionInfo.getPlayerId());
		logger.info("{}重新进入{}游戏:所在位置{}, 请求游戏桌{}", player.getNickname(), gameItem.getName(), gameSessionInfo.getAddress().getAddrNo(), this.getAddrNo());
		
		RtnGameInfoCmd rtnCmd = getGameInfo();
		rtnCmd.setAddress(gameSessionInfo.getAddress().getAddrNo());
		gameSession.write(rtnCmd);
	}
	protected RtnGameInfoCmd getGameInfo() {
		RtnGameInfoCmd rtnCmd = new RtnGameInfoCmd();
		rtnCmd.setPauseMs(this.getPauseTime());
		rtnCmd.setManagerId(this.getManagerId());
		rtnCmd.setAddress(this.getAddrNo());
		rtnCmd.setSeatPlayingMap(this.getSeatMasterPlayer());
		return rtnCmd;
	}
	
	public Long getManagerId() {
		if(manager.get() == null) {
			return null;
		}
		return manager.get().getId();	
	}
	
	public Player getManager() {
		return manager.get();	
	}
	
	public GameSeat getGameSeat(int position) {
		return seatPlayingMap.get(position);
	}
	public GameSeat getIdelGameSeat() {
		for(GameSeat gameSeat : seatPlayingMap.values()) {
			if(gameSeat.isIdle()) {
				return gameSeat;
			}
		}
		return null;
	}
	protected GameSeat buildGameSeat(int position){
		return new GameSeat(this, position);
	}
	
	/***
	 * key position
	 * @return
	 */
	public HashMap<String, ? extends PlayerVo> getSeatMasterPlayer(){
		HashMap<String, PlayerVo> map = new HashMap<String, PlayerVo>(seatPlayingMap.size());
		for(Integer position : seatPlayingMap.keySet()) {
			GameSeat gameSeat = seatPlayingMap.get(position);
			SeatPlayer master = gameSeat.getMaster().get();
			if(master != null && master.getPlayer() != null) {
				PlayerVo vo = master.getPlayer().valueOfPlayerVo();
				map.put(position + "", vo);
			}
		}
		return map;
	}

	/***
	 * 申请成为管理员
	 * 
	 * 当无管理员时第一个自动成功管理员
	 * 
	 * @param playerId
	 */
	public boolean applyManager(GameSessionInfo gameSessionInfo, ReqApplyManagerCmd reqCmd) {
		this.operatorVerfy();
		
		//判断是否是在游戏桌中
		long playerId = gameSessionInfo.getPlayerId();
		Player player = playerMap.get(playerId);
		if(manager.get() != null) {
			if(manager.get().getId() == playerId) {
				throw new BizException(String.format("%s已是管理员, 不可再申请", playerId));
			}
			
			//向这管理员发送告知
			PushNotifyApplyManagerCmd pushCmd = new PushNotifyApplyManagerCmd();
			pushCmd.setPlayerId(playerId);
			pushCmd.setHeadPic(player.getHeadPic());
			pushCmd.setNickname(player.getNickname());
			manager.get().getOnline().push(pushCmd);
			logger.info("{}向管理员{}发送更换管理员申请", player.getNickname(), manager.get().getNickname());
			return false;
		}
		
		if(manager.compareAndSet(null, player)) {
			manager.set(player);
			logger.info("{}在游戏桌:{}--{},抢到管理员之位", player.getNickname(), deskItem.getPlayKind(), this.getAddrNo());
			
			PushApplyManagerCmd pushCmd = reqCmd.valueOfPushApplyManagerCmd();
			pushCmd.setPlayerId(playerId);
			pushCmd.setNickname(player.getNickname());
			pushCmd.setHeadPic(player.getHeadPic());
			this.broadcast(pushCmd, gameSessionInfo.getPlayerId());
			
			return true;
		}

		throw new BizException(String.format("管理员之位已被%s强走了，请重试吧", manager.get().getNickname()));
	}
	
	/***
	 * 更改管理员
	 * @param player
	 * @param playerId 为空时，表示无管理人员
	 */
	public void changeManager(GameSessionInfo gameSessionInfo, ReqChangeManagerCmd reqCmd) {
		checkAndGetManager(gameSessionInfo.getPlayerId());
		
		if(reqCmd.getOtherId() != null && gameSessionInfo.getPlayerId() == reqCmd.getOtherId()) {
			throw new BizException(String.format("管理员不可以将自己改为管理员"));
		}
		if(reqCmd.getOtherId() != null) {
			Player other = playerMap.get(reqCmd.getOtherId());
			if(other == null) {
				throw new BizException(String.format("非法请求，不在游戏桌中"));
			}
			manager.set(other);
			PushChangeManagerCmd pushCmd = reqCmd.valueOfPushChatMultiCmd();
			pushCmd.setPlayerId(gameSessionInfo.getPlayerId());
			pushCmd.setNickname(other.getNickname());
			pushCmd.setHeadPic(other.getHeadPic());
			this.broadcast(pushCmd, gameSessionInfo.getPlayerId());
		}
		else {
			//取消管理员之位
			manager.set(null);
			PushChangeManagerCmd pushCmd = reqCmd.valueOfPushChatMultiCmd();
			this.broadcast(pushCmd, gameSessionInfo.getPlayerId());
		}
		logger.info("{}在游戏桌:{}--{},将管理员之位传给:{}", gameSessionInfo.getPlayerId(), deskItem.getPlayKind(), this.getAddrNo(), reqCmd.getOtherId());
	}
	protected void checkAndGetManager(long managerId) {
		if(manager.get() == null) {
			throw new BizException(String.format("还没设置管理员"));
		}
		if(manager.get().getId() != managerId) {
			throw new BizException(String.format("%s不是管理员", managerId));
		}
	}
	
	
	/***游戏暂停****/
	public void pause(GameSessionInfo gameSessionInfo, ReqPauseCmd reqCmd) {
		this.operatorVerfy();
		checkAndGetManager(gameSessionInfo.getPlayerId());
		
		Player player = playerMap.get(gameSessionInfo.getPlayerId());
		pause(reqCmd.getSeconds());
		
		logger.info("{}在游戏桌:{}--{},暂停游戏{}秒", player.getNickname(), deskItem.getPlayKind(), this.getAddrNo(), reqCmd.getSeconds());
	}
	
	/***游戏取消暂停(恢复正常)****/
	public void resume(GameSessionInfo gameSessionInfo, ReqResumeCmd reqCmd) {
		this.operatorVerfy();
		checkAndGetManager(gameSessionInfo.getPlayerId());
		
		Player player = playerMap.get(gameSessionInfo.getPlayerId());
		resume();
		
		logger.info("{}在游戏桌:{}--{},恢复游戏", player.getNickname(), deskItem.getPlayKind(), this.getAddrNo());
	}
	
	public void approveBroadcastLive(GameSessionInfo gameSessionInfo, ReqApproveBroadcastLiveCmd reqCmd) {
		if(getManagerId() == null) {
			throw new BizException(String.format("%s不是管理员，不可以同意申请直播", gameSessionInfo.getPlayerId()));
		}
		if(getManagerId() != gameSessionInfo.getPlayerId()) {
			throw new BizException(String.format("%s不是管理员，不可以同意申请直播", gameSessionInfo.getPlayerId()));
		}
		
		int position = reqCmd.getPosition();
		GameSeat gameSeat = this.seatPlayingMap.get(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s席位没有人入座", position));
		}
		if(gameSeat.isBroadcasting()) {
			throw new BizException(String.format("%s席位已经是可以直播的啦", position));
		}
		if(!gameSeat.isApplyBroadcasted()) {
			throw new BizException(String.format("%s席位没有人申请直播", position));
		}
		gameSeat.broadcasting = true;
		gameSeat.applyBroadcasted = false;
		
		PushApproveBroadcastLiveCmd pushCmd = reqCmd.valueOfPushApproveBroadcastLiveCmd();
		pushCmd.setPosition(position);
		broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}

	protected void checkPosistion(List<Integer> positionList) throws BizException{
		for(int position : positionList) {
			if(this.seatPlayingMap.containsKey(position)) {
				throw new BizException(String.format("%s席位没有人入座", position));
			}
		}
	}

	protected SeatPlayer getSeatPlayer(long playerId) {
		for(GameSeat gameSeat : this.seatPlayingMap.values()) {
			SeatPlayer seatPlayer = gameSeat.getSeatPlayer(playerId);
			if(seatPlayer != null) {
				return seatPlayer;
			}
		}
		return null;
	}
}
