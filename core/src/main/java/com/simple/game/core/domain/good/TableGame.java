package com.simple.game.core.domain.good;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.push.game.PushApplyManagerCmd;
import com.simple.game.core.domain.cmd.push.game.PushChangeManagerCmd;
import com.simple.game.core.domain.cmd.push.seat.PushBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.rtn.seat.RtnGameSeatInfoCmd;
import com.simple.game.core.domain.dto.BaseDesk;
import com.simple.game.core.domain.dto.GameSeat;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.SeatPlayer;
import com.simple.game.core.domain.dto.TableDesk;
import com.simple.game.core.domain.dto.config.DeskItem;
import com.simple.game.core.domain.dto.config.GameItem;
import com.simple.game.core.domain.ext.Chat;
import com.simple.game.core.domain.ext.Gift;
import com.simple.game.core.exception.BizException;

/***
 * 含有席位的游戏
 * 
 * @author zhibozhang
 *
 */
public abstract class TableGame extends BaseGame{
	private final static Logger logger = LoggerFactory.getLogger(TableGame.class);
	
	public TableGame(GameItem gameItem, DeskItem deskItem) {
		super(gameItem, deskItem);
	}

	@Override
	protected BaseDesk buildDesk(){
		return new TableDesk(this);
	}
	
	/***
	 * 选择某个席位座下
	 * 
	 * @param player
	 * @param position
	 */
	public final RtnGameSeatInfoCmd sitdown(long playerId, int position, OutParam<Player> outParam) {
		this.operatorVerfy();
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		
		gameSeat.sitdown(player);
		outParam.setParam(player);
		
		logger.info("{}进入{}游戏:并在游戏桌{},席位{}坐下", player.getNickname(), gameItem.getName(), baseDesk.getAddrNo(), position);
		return gameSeat.getGameSeatInfo();
	}
	
	/***
	 * 获取桌位玩家
	 * 需要页面这个接口的访问频率
	 */
	public List<SeatPlayer> getSeatPlayerList(int position, int fromPage) {
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		List<SeatPlayer> list = gameSeat.getSeatPlayerList();
		List<SeatPlayer> result = new ArrayList<SeatPlayer>();
		int fromIndex = fromPage * PAGE_SIZE;
		int toIndex = fromIndex + PAGE_SIZE;
		for(int i=fromIndex; i<list.size() && i<toIndex; i++) {
			result.add(list.get(i));
		}
		return result;
	}
	public List<SeatPlayer> getAssistantList(int position) {
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		return gameSeat.getAssistantList();
	}

	/***
	 * 快速坐下
	 * 
	 * @param player
	 */
	public final RtnGameSeatInfoCmd quickSitdown(long playerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		preQuickSitdown(playerId);
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getIdelGameSeat();
		if(gameSeat == null) {
			throw new BizException(String.format("已经没有空闲的席位了"));
		}
		
		outParam.setParam(player);
		gameSeat.sitdown(player);
		logger.info("{}进入{}游戏:并在游戏桌{},抢到{}席位并坐下", player.getNickname(), gameItem.getName(), baseDesk.getAddrNo(), gameSeat.getPosition());
		return gameSeat.getGameSeatInfo();
	}
	protected void preQuickSitdown(long playerId) {}
	
	/***
	 * 申请辅助
	 * @param playerId
	 */
	public void applyAssistant(long playerId, int position, OutParam<Player> outParam) {
		this.operatorVerfy();
		
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("已经没有空闲的席位了"));
		}
		
		//是否有进入的限制条件
		gameSeat.applyAssistant(player);
		outParam.setParam(player);
		logger.info("{}在游戏席位: {}--{}--{}申请辅助", player.getNickname(), gameItem.getName(), baseDesk.getAddrNo(), position);
		//等待主席位应答
	}
	
	/***
	 * 同意XXX成为辅助
	 * @param player
	 * @param id
	 */
	public void approveApplyAssistant(long masterId, int position, long playerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		Player master = getGameDesk().getPlayer(masterId);
		if(master == null) {
			throw new BizException(String.format("%s不在游戏中", masterId));
		}
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("已经没有空闲的席位了"));
		}
		
		gameSeat.approveApplyAssistant(master, player);
		outParam.setParam(player);
		logger.info("{}同意席位:{}--{}--{}的{}成为辅助", master.getNickname(), gameItem.getName(), baseDesk.getAddrNo(), position, playerId);
	}
	protected abstract void onApproveApplyAssistant(SeatPlayer player);
	
	/***
	 * 从席位中站起来
	 * @param player
	 */
	public final void standUp(long playerId, int position, OutParam<Player> outParam) {
		this.operatorVerfy();
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		
		gameSeat.standUp(player);
		outParam.setParam(player);
		logger.info("{}在席位:{}--{}--{}站起来了", player.getNickname(), gameItem.getName(), baseDesk.getAddrNo(), position);
	}
	
	
	public void stopAssistant(long playerId, int position) {
		this.operatorVerfy();
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		gameSeat.stopAssistant(player);
		logger.info("{}在席位:{}--{}--{}停止申请助手", player.getNickname(), gameItem.getName(), baseDesk.getAddrNo(), position);
	}
	public void stopOnlooker(long playerId, int position) {
		this.operatorVerfy();
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		gameSeat.stopOnlooker(player);
		logger.info("{}在席位:{}--{}--{}停止申请旁观者", player.getNickname(), gameItem.getName(), baseDesk.getAddrNo(), position);
	}
	public void bootAssistant(long playerId, int position) {
		this.operatorVerfy();
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		gameSeat.bootAssistant(player);
		logger.info("{}在席位:{}--{}--{}开启申请助手", player.getNickname(), gameItem.getName(), baseDesk.getAddrNo(), position);

	}
	public void bootOnlooker(long playerId, int position) {
		this.operatorVerfy();
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		gameSeat.bootOnlooker(player);
		logger.info("{}在席位:{}--{}--{}开启申请旁观者", player.getNickname(), gameItem.getName(), baseDesk.getAddrNo(), position);
	}
	
	/***
	 * 设置(下一轮)主席位继任人
	 * @param player
	 */
	public final void setSeatSuccessor(long masterId, int position, long playerId) {
		this.operatorVerfy();
		Player master = getGameDesk().getPlayer(masterId);
		if(master == null) {
			throw new BizException(String.format("%s不在游戏中", masterId));
		}
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		
		gameSeat.setSeatSuccessor(master, player);
		logger.info("{}在席位:{}--{}--{}将主席位继任者传给{}成功", player.getNickname(), gameItem.getName(), baseDesk.getAddrNo(), position, playerId);
	}
	
	/***
	 * 强制席中的某人站起
	 * @param masterId
	 * @param playerId
	 */
	public void forceStandUp(long masterId, int position, long playerId, OutParam<Player> outParam) {
		this.operatorVerfy();
		Player master = getGameDesk().getPlayer(masterId);
		if(master == null) {
			throw new BizException(String.format("%s不在游戏中", masterId));
		}
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("已经没有空闲的席位了"));
		}
		
		gameSeat.forceStandUp(master, player);
		outParam.setParam(player);
		logger.info("{}在席位:{}--{}--{}请 {}站起", master.getNickname(), gameItem.getName(), baseDesk.getAddrNo(), position, playerId);
	}
	
	/***TODO
	 * 在席位中对某个席位进行打赏
	 * @param player
	 */
	public void reward(long playerId, List<Integer> positionList, Gift gift, OutParam<Player> outParam) {
		this.operatorVerfy();
//		OutParam<Player> outParam = OutParam.build();
//		List<PushRewardCmd> result = getGameDesk().reward(playerId, positionList, gift, outParam);
//		for(PushRewardCmd pushCmd : result) {
//			this.broadcast(pushCmd, playerId);
//		}
		logger.info("{}对席位:{}--{}--{}打赏{}礼物", outParam.getParam().getNickname(), gameItem.getName(), baseDesk.getAddrNo(), positionList, gift.getKind());
	}
	
	
	/*** TODO 聊天****/
	public void chat(long playerId, List<Integer> positionList, Chat message, OutParam<Player> outParam) {
		this.operatorVerfy();
//		OutParam<Player> outParam = OutParam.build();
//		List<PushChatCmd> result = getGameDesk().chat(playerId, positionList, message, outParam);
//		for(PushChatCmd pushCmd : result) {
//			this.broadcast(pushCmd, playerId);
//		}
		logger.info("{}在游戏桌:{}--{},对席位{}聊天{}", outParam.getParam().getNickname(), gameItem.getName(), baseDesk.getAddrNo(), positionList, message.getKind());
	}
	

	/***申请直播****/
	public boolean applyBroadcastLive(long playerId) {
		this.operatorVerfy();
		OutParam<SeatPlayer> outParam = OutParam.build();
		boolean result = getGameDesk().applyBroadcastLive(playerId, outParam);
		logger.info("{}申请直播席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), baseDesk.getAddrNo(), outParam.getParam().getGameSeat().getPosition());
		return result;
	}
	/***取消直播****/
	public void cancleBroadcastLive(long playerId) {
		this.operatorVerfy();
		OutParam<SeatPlayer> outParam = OutParam.build();
		PushCancelBroadcastLiveCmd result = getGameDesk().cancelBroadcastLive(playerId, outParam);
		if(result != null) {
			this.broadcast(result, playerId);
		}
		logger.info("{}申请直播席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), baseDesk.getAddrNo(), outParam.getParam().getGameSeat().getPosition());
	}
	/***直播****/
	public void broadcastLive(long playerId, byte[] data) {
		this.operatorVerfy();
		OutParam<SeatPlayer> outParam = OutParam.build();
		PushBroadcastLiveCmd pushCmd = getGameDesk().broadcastLive(playerId, data, outParam);
		this.broadcast(pushCmd, playerId);
		logger.info("{}正在直播:席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), baseDesk.getAddrNo(), outParam.getParam().getGameSeat().getPosition());
	}
	/***
	 * 管理员同意XXX的直播
	 * @param player
	 * @param id
	 */
	public void approveBroadcastLive(long managerId, int position) {
		this.operatorVerfy();
		getGameDesk().approveBroadcastLive(managerId, position);
		logger.info("{}同意席位:{}--{}--{}的的直播", outParam.getParam().getNickname(), gameItem.getName(), baseDesk.getAddrNo(), position);
	}
	
	/***
	 * 申请成为管理员
	 * 
	 * 当无管理员时第一个自动成功管理员
	 * 
	 * @param player
	 * @param position
	 */
	public void applyManager(long playerId) {
		this.operatorVerfy();
		OutParam<Player> outParam = OutParam.build();
		PushApplyManagerCmd result = getGameDesk().applyManager(playerId, outParam);
		if(result != null) {
			this.broadcast(result, playerId);
			logger.info("{}在游戏桌:{}--{},申请管理员", outParam.getParam().getNickname(), gameItem.getName(), baseDesk.getAddrNo());
		}
	}
	
	/***
	 * 更改管理员
	 * @param player
	 * @param playerId 为空时，表示无管理人员
	 */
	public void changeManager(long managerId, Long playerId) {
		this.operatorVerfy();
		OutParam<Player> outParam = OutParam.build();
		PushChangeManagerCmd result = getGameDesk().changeManager(managerId, playerId, outParam);
		this.broadcast(result, managerId);
		logger.info("{}在游戏桌:{}--{},将管理员之位传给:{}", outParam.getParam().getNickname(), gameItem.getName(), baseDesk.getAddrNo(), playerId);
	}
	
	/***
	 * 踢人
	 * @param player
	 * @param id
	 */
	public void kickout(long managerId, long playerId) {
		this.operatorVerfy();
		Long oldManagerId = getGameDesk().getManagerId(); 
		if(oldManagerId == null) {
			throw new BizException(String.format("不存在管理员"));
		}
		if(managerId != oldManagerId) {
			throw new BizException(String.format("%s不是管理员", managerId));
		}
		kickout(playerId);
	}
	
	@Override
	protected void preLeft(long playerId) {
		SeatPlayer seatPlayer = getGameDesk().getSeatPlayer(playerId);
		if(seatPlayer != null) {
			PushStandupCmd pushCmd = seatPlayer.getGameSeat().standUp(seatPlayer.getPlayer());
			//广播进入信息
			this.broadcast(pushCmd, playerId);
			logger.info("强制在席位:{}--{}--{},将{}踢走", gameItem.getName(), baseDesk.getAddrNo(), seatPlayer.getGameSeat().getPosition(), seatPlayer.getPlayer().getNickname());
		}
	}
	
	public TableDesk getGameDesk() {
		return (TableDesk)baseDesk;
	}
}
