package com.simple.game.core.domain.good;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.push.game.notify.PushNotifyApplyManagerCmd;
import com.simple.game.core.domain.cmd.rtn.game.RtnGameInfoCmd;
import com.simple.game.core.domain.cmd.rtn.seat.RtnGameSeatInfoCmd;
import com.simple.game.core.domain.dto.GameSeat;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.SeatPlayer;
import com.simple.game.core.domain.dto.TableDesk;
import com.simple.game.core.domain.dto.config.DeskItem;
import com.simple.game.core.domain.dto.config.GameItem;
import com.simple.game.core.domain.ext.Chat;
import com.simple.game.core.domain.ext.Gift;
import com.simple.game.core.exception.BizException;

import lombok.Getter;

/***
 * 含有席位的游戏
 * 
 * TableGame与TableDesk基本等同，但是TableGame更注游戏外围管理部份，TableDesk更接近内部游戏
 * 
 * @author zhibozhang
 *
 */
@Getter
public abstract class TableGame extends BaseGame{
	private final static Logger logger = LoggerFactory.getLogger(TableGame.class);
	
	/****管理员***/
	protected final AtomicReference<Player> manager = new AtomicReference<Player>();
	
	/***所在的游戏桌****/
	protected TableDesk tableDesk;
	protected DeskItem deskItem;
	/***游戏配置****/
	protected GameItem gameItem;
	
	public TableGame(GameItem gameItem, DeskItem deskItem) {
		super(gameItem);
		this.deskItem = deskItem;
		this.gameItem = gameItem;
		this.tableDesk = buildDesk();
		preInit(gameItem, deskItem);
	}
	protected void preInit(GameItem gameItem, DeskItem deskItem) {};

	protected TableDesk buildDesk(){
		return new TableDesk(this);
	}
	
	protected void preJoin(Player player) {
		//不需要加锁判断，减少时死锁，提高性能，允许极少量的误差
		if(playerMap.size()+1 > deskItem.getMaxPersion()) {
			throw new BizException(String.format("人员已挤不下去了(已有%s)", deskItem.getMaxPersion()));
		}
	}
	
	protected RtnGameInfoCmd rejoin(Player player) {
		if(!player.getAddress().getAddrNo().startsWith(tableDesk.getAddrNo())) {
			throw new BizException(String.format("已在%s, 不可以再进入%s", player.getAddress().getAddrNo(), tableDesk.getAddrNo()));
		}
		
		RtnGameInfoCmd rtnCmd = getGameInfo();
		rtnCmd.setAddress(player.getAddress().getAddrNo());
		return rtnCmd;
	}
	
	protected RtnGameInfoCmd getGameInfo() {
		RtnGameInfoCmd rtnCmd = new RtnGameInfoCmd();
		rtnCmd.setPauseMs(this.getPauseTime());
		rtnCmd.setManagerId(this.getManagerId());
		rtnCmd.setAddress(tableDesk.getAddrNo());
		return rtnCmd;
	}
	
	public int getDeskNo() {
		return tableDesk.getDeskNo();
	}
	
	/***
	 * 进入游戏
	 */
	public RtnGameInfoCmd join(Player player) {
		this.operatorVerfy();
		if(player.getAddress() != null) {
			logger.info("{}重新进入{}游戏:所在位置{}, 请求游戏桌{}", player.getNickname(), gameItem.getName(), player.getAddress(), tableDesk.getAddrNo());
			return rejoin(player);
		}
		
		//是否有进入的限制条件
		this.preJoin(player);
		
		playerMap.put(player.getId(), player);
		player.setAddress(tableDesk);
		
		RtnGameInfoCmd rtnCmd = getGameInfo();
		rtnCmd.setPlayerId(player.getId());
		logger.info("{}进入{}游戏, 返回:{} ", player.getNickname(), gameItem.getName(), rtnCmd.toLogStr());
		return rtnCmd;
	}
	
	
	/***
	 * 选择某个席位座下
	 * 
	 * @param player
	 * @param position
	 */
	public RtnGameSeatInfoCmd sitdown(long playerId, int position, OutParam<Player> outParam) {
		this.operatorVerfy();
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		preSitdown(player);
		outParam.setParam(player);
		return tableDesk.sitdown(player, position);
	}
	
	protected void preSitdown(Player player) {};
	
	/***
	 * 获取桌位玩家
	 * 需要页面这个接口的访问频率
	 */
	public List<SeatPlayer> getSeatPlayerList(int position, int fromPage) {
		return tableDesk.getSeatPlayerList(position, fromPage);
	}
	public List<SeatPlayer> getAssistantList(int position) {
		return tableDesk.getAssistantList(position);
	}

	/***
	 * 快速坐下
	 * 
	 * @param player
	 */
	public final RtnGameSeatInfoCmd quickSitdown(long playerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		preSitdown(player);
		outParam.setParam(player);
		return tableDesk.quickSitdown(player);
	}
	
	
	/***
	 * 申请辅助
	 * @param playerId
	 */
	public void applyAssistant(long playerId, int position, OutParam<Player> outParam) {
		this.operatorVerfy();
		
		Player player = playerMap.get(playerId);;
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		outParam.setParam(player);
		tableDesk.applyAssistant(player, position);;
	}
	
	/***
	 * 同意XXX成为辅助
	 * @param player
	 * @param id
	 */
	public void approveApplyAssistant(long masterId, int position, long playerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		Player master = playerMap.get(masterId);
		if(master == null) {
			throw new BizException(String.format("%s不在游戏中", masterId));
		}
		Player player = playerMap.get(playerId);;
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		outParam.setParam(player);
		tableDesk.approveApplyAssistant(master, position, player, outParam);
	}
	
	/***
	 * 从席位中站起来
	 * @param player
	 */
	public final void standUp(long playerId, int position, OutParam<Player> outParam) {
		this.operatorVerfy();
		Player player = playerMap.get(playerId);;
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		outParam.setParam(player);
		tableDesk.standUp(player, position);
	}
	
	
	public void stopAssistant(long playerId, int position) {
		this.operatorVerfy();
		Player player = playerMap.get(playerId);;
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		tableDesk.stopAssistant(player, position);
	}
	public void stopOnlooker(long playerId, int position) {
		this.operatorVerfy();
		Player player = playerMap.get(playerId);;
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		tableDesk.stopOnlooker(player, position);
	}
	public void bootAssistant(long playerId, int position) {
		this.operatorVerfy();
		Player player = playerMap.get(playerId);;
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		tableDesk.bootAssistant(player, position);
	}
	public void bootOnlooker(long playerId, int position) {
		this.operatorVerfy();
		Player player = playerMap.get(playerId);;
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		tableDesk.bootOnlooker(player, position);
	}
	
	/***
	 * 申请(下一轮)主席位继任人
	 * @param player
	 */
	public final void applySeatSuccessor(long playerId, int position, OutParam<Player> outParam) {
		this.operatorVerfy();
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		outParam.setParam(player);
		tableDesk.applySeatSuccessor(player, position);
	}
	
	/***
	 * 设置(下一轮)主席位继任人
	 * @param player
	 */
	public final void setSeatSuccessor(long masterId, int position, long playerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		Player master = playerMap.get(masterId);
		if(master == null) {
			throw new BizException(String.format("%s不在游戏中", masterId));
		}
		Player player = playerMap.get(playerId);;
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		outParam.setParam(player);
		tableDesk.setSeatSuccessor(master, position, player, outParam);
	}
	
	/***
	 * 强制席中的某人站起
	 * @param masterId
	 * @param playerId
	 */
	public void forceStandUp(long masterId, int position, long playerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		Player master = playerMap.get(masterId);
		if(master == null) {
			throw new BizException(String.format("%s不在游戏中", masterId));
		}
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		outParam.setParam(player);
		tableDesk.forceStandUp(master, position, player);
	}
	
	/***TODO
	 * 在席位中对某个席位进行打赏
	 * @param player
	 */
	public void reward(long playerId, List<Integer> positionList, Gift gift, OutParam<Player> outParam) {
		this.operatorVerfy();
//		OutParam<Player> outParam = OutParam.build();
//		List<PushRewardCmd> result = tableDesk.reward(playerId, positionList, gift, outParam);
//		for(PushRewardCmd pushCmd : result) {
//			this.broadcast(pushCmd, playerId);
//		}
		logger.info("{}对席位:{}--{}--{}打赏{}礼物", outParam.getParam().getNickname(), deskItem.getPlayKind(), tableDesk.getAddrNo(), positionList, gift.getKind());
	}
	
	
	/*** TODO 聊天****/
	public void chat(long playerId, List<Integer> positionList, Chat message, OutParam<Player> outParam) {
		this.operatorVerfy();
//		OutParam<Player> outParam = OutParam.build();
//		List<PushChatCmd> result = tableDesk.chat(playerId, positionList, message, outParam);
//		for(PushChatCmd pushCmd : result) {
//			this.broadcast(pushCmd, playerId);
//		}
		logger.info("{}在游戏桌:{}--{},对席位{}聊天{}", outParam.getParam().getNickname(), deskItem.getPlayKind(), tableDesk.getAddrNo(), positionList, message.getKind());
	}
	
	public Long getManagerId() {
		if(manager.get() == null) {
			return null;
		}
		return manager.get().getId();	
	}

	/***申请直播****/
	public boolean applyBroadcastLive(long playerId, OutParam<SeatPlayer> outParam) {
		this.operatorVerfy();
		if(manager.get() == null) {
			throw new BizException(String.format("没有管理员，不可以同意申请直播"));
		}
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		boolean result = tableDesk.applyBroadcastLive(player, outParam);
		if(!result) {
			PushNotifyApplyManagerCmd pushCmd = new PushNotifyApplyManagerCmd();
			pushCmd.setPlayerId(playerId);
			pushCmd.setNickname(player.getNickname());
			pushCmd.setHeadPic(player.getHeadPic());
			manager.get().getOnline().push(pushCmd);
			logger.info("{}向管理员{}发送主播申请", player.getNickname(), manager.get().getNickname());
		}
		
		logger.info("{}申请直播席位:{}--{}--{}", player.getNickname(), deskItem.getPlayKind(), tableDesk.getAddrNo(), outParam.getParam().getGameSeat().getPosition());
		return result;
	}
	/***取消直播****/
	public void cancleBroadcastLive(long playerId) {
		this.operatorVerfy();
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		OutParam<SeatPlayer> outParam = OutParam.build();
		tableDesk.cancelBroadcastLive(player, outParam);
	}
	/***直播****/
	public void broadcastLive(long playerId, byte[] data) {
		this.operatorVerfy();
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		OutParam<SeatPlayer> outParam = OutParam.build();
		tableDesk.broadcastLive(player, outParam);
	}
	/***
	 * 管理员同意XXX的直播
	 * @param player
	 * @param id
	 */
	public void approveBroadcastLive(long managerId, int position) {
		this.operatorVerfy();
		if(manager.get() == null) {
			throw new BizException(String.format("还没设置管理员"));
		}
		if(manager.get().getId() != managerId) {
			throw new BizException(String.format("%s不是管理员", managerId));
		}
		
		tableDesk.approveBroadcastLive(position);
		logger.info("{}同意席位:{}--{}--{}的的直播", manager.get().getNickname(), deskItem.getPlayKind(), tableDesk.getAddrNo(), position);
	}
	
	/***
	 * 申请成为管理员
	 * 
	 * 当无管理员时第一个自动成功管理员
	 * 
	 * @param playerId
	 */
	public boolean applyManager(long playerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		
		//判断是否是在游戏桌中
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("非法请求，不在游戏桌中"));
		}
		
		outParam.setParam(player);
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
			logger.info("{}在游戏桌:{}--{},抢到管理员之位", outParam.getParam().getNickname(), deskItem.getPlayKind(), tableDesk.getAddrNo());
			return true;
		}

		throw new BizException(String.format("管理员之位已被%s强走了，请重试吧", manager.get().getNickname()));
	}
	
	public void checkAndGetManager(long managerId, OutParam<Player> outParam) {
		if(manager.get() == null) {
			throw new BizException(String.format("还没设置管理员"));
		}
		if(manager.get().getId() != managerId) {
			throw new BizException(String.format("%s不是管理员", managerId));
		}
		outParam.setParam(manager.get());
	}
	
	/***
	 * 更改管理员
	 * @param player
	 * @param playerId 为空时，表示无管理人员
	 */
	public void changeManager(long managerId, Long playerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		
		checkAndGetManager(managerId, outParam);
		if(playerId != null && managerId == playerId) {
			throw new BizException(String.format("管理员不可以将自己改为管理员"));
		}
		if(playerId != null) {
			if(managerId == playerId) {
				throw new BizException(String.format("管理员不可以将自己改为管理员"));
			}
			Player player = playerMap.get(playerId);
			if(player == null) {
				throw new BizException(String.format("非法请求，不在游戏桌中"));
			}
			manager.set(player);
		}
		else {
			//取消管理员之位
			manager.set(null);
		}
		logger.info("{}在游戏桌:{}--{},将管理员之位传给:{}", outParam.getParam().getNickname(), deskItem.getPlayKind(), tableDesk.getAddrNo(), playerId);
	}
	
	/***
	 * 踢人
	 * @param player
	 * @param id
	 */
	public void kickout(long managerId, long playerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		checkAndGetManager(managerId, outParam);
		kickout(playerId, outParam);
	}
	
	/***游戏暂停****/
	public void pause(long managerId, int seconds, OutParam<Player> outParam) {
		this.operatorVerfy();
		checkAndGetManager(managerId, outParam);
		super.pause(seconds);
		
		logger.info("{}在游戏桌:{}--{},暂停游戏{}秒", outParam.getParam().getNickname(), deskItem.getPlayKind(), tableDesk.getAddrNo(), managerId);
	}
	/***游戏取消暂停(恢复正常)****/
	public void resume(long managerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		checkAndGetManager(managerId, outParam);
		super.resume();
		logger.info("{}在游戏桌:{}--{},恢复游戏", outParam.getParam().getNickname(), deskItem.getPlayKind(), tableDesk.getAddrNo(), managerId);
	}
	
	@Override
	protected void preLeft(long playerId) {
		Player player = playerMap.get(playerId);
		if(player != null) {
			if(player.getAddress() instanceof GameSeat) {
				int position = ((GameSeat)player.getAddress()).getPosition();
				tableDesk.standUp(player, position);
				logger.info("强制在席位:{},将{}踢走", player.getAddress(), player.getNickname());
			}
		}
	}
}
