package com.simple.game.core.domain.good;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.push.PushApplyBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.push.PushApplyManagerCmd;
import com.simple.game.core.domain.cmd.push.PushApproveBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.push.PushBootAssistantCmd;
import com.simple.game.core.domain.cmd.push.PushBootOnlookerCmd;
import com.simple.game.core.domain.cmd.push.PushBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.push.PushCancelBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.push.PushChangeManagerCmd;
import com.simple.game.core.domain.cmd.push.PushChatCmd;
import com.simple.game.core.domain.cmd.push.PushNotifyApplyAssistantCmd;
import com.simple.game.core.domain.cmd.push.PushRewardCmd;
import com.simple.game.core.domain.cmd.push.PushSetSeatSuccessorCmd;
import com.simple.game.core.domain.cmd.push.PushSitdownCmd;
import com.simple.game.core.domain.cmd.push.PushStandupCmd;
import com.simple.game.core.domain.cmd.push.PushStopAssistantCmd;
import com.simple.game.core.domain.cmd.push.PushStopOnlookerCmd;
import com.simple.game.core.domain.cmd.rtn.RtnGameSeatCmd;
import com.simple.game.core.domain.cmd.rtn.RtnSeatInfoListCmd;
import com.simple.game.core.domain.dto.BaseDesk;
import com.simple.game.core.domain.dto.GameSeat;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.SeatPlayer;
import com.simple.game.core.domain.dto.TableDesk;
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
	private static Logger logger = LoggerFactory.getLogger(TableGame.class);
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
	public final synchronized RtnGameSeatCmd sitdown(long playerId, int position) {
		this.operatorVerfy();
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		
		OutParam<SeatPlayer> outParam = OutParam.build();
		PushSitdownCmd pushCmd = gameSeat.sitdown(player);
		//广播进入信息
		this.broadcast(pushCmd, playerId);
		
		logger.info("{}进入{}游戏:并在游戏桌{},席位{}坐下", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), deskItem.getNumber(), position);
		return outParam.getParam().getGameSeat().getRtnGameSeatCmd();
	}
	
	public RtnSeatInfoListCmd getRtnSeatInfoListCmd(int position) {
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		return gameSeat.getRtnSeatInfoListCmd();
	}

	/***
	 * 快速坐下
	 * 
	 * @param player
	 */
	public final synchronized RtnGameSeatCmd quickSitdown(long playerId) {
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
		
		PushSitdownCmd pushCmd = gameSeat.sitdown(player);
		//广播进入信息
		this.broadcast(pushCmd, playerId);
		
		logger.info("{}进入{}游戏:并在游戏桌{},抢到{}席位并坐下", player.getNickname(), gameItem.getName(), deskItem.getNumber(), gameSeat.getPosition());
		return gameSeat.getRtnGameSeatCmd();
	}
	protected void preQuickSitdown(long playerId) {}
	
	/***
	 * 申请辅助
	 * @param playerId
	 */
	public synchronized void applyAssistant(long playerId, int position) {
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
		logger.info("{}在游戏席位: {}--{}--{}申请辅助", player.getNickname(), gameItem.getName(), deskItem.getNumber(), position);
		//等待主席位应答
	}
	
	/***
	 * 同意XXX成为辅助
	 * @param player
	 * @param id
	 */
	public void approveApplyAssistant(long masterId, int position, long playerId) {
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
		
		OutParam<SeatPlayer> outParam = OutParam.build();
		PushNotifyApplyAssistantCmd pushCmd = gameSeat.approveApplyAssistant(master, player);
		this.broadcast(pushCmd, masterId);
		logger.info("{}同意席位:{}--{}--{}的{}成为辅助", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), deskItem.getNumber(), outParam.getParam().getGameSeat().getPosition(), playerId);
	}
	protected abstract void onApproveApplyAssistant(SeatPlayer player);
	
	/***
	 * 从席位中站起来
	 * @param player
	 */
	public final synchronized void standUp(long playerId, int position) {
		this.operatorVerfy();
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		
		PushStandupCmd pushCmd = gameSeat.standUp(player);
		//广播进入信息
		this.broadcast(pushCmd, playerId);
		logger.info("{}在席位:{}--{}--{}站起来了", player.getNickname(), gameItem.getName(), deskItem.getNumber(), position);
	}
	
	
	public synchronized void stopAssistant(long playerId, int position) {
		this.operatorVerfy();
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		PushStopAssistantCmd pushCmd = gameSeat.stopAssistant(player);
		//广播进入信息
		this.broadcast(pushCmd, playerId);
		logger.info("{}在席位:{}--{}--{}停止申请助手", player.getNickname(), gameItem.getName(), deskItem.getNumber(), position);
	}
	public synchronized void stopOnlooker(long playerId, int position) {
		this.operatorVerfy();
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		PushStopOnlookerCmd pushCmd = gameSeat.stopOnlooker(player);
		//广播进入信息
		this.broadcast(pushCmd, playerId);
		logger.info("{}在席位:{}--{}--{}停止申请旁观者", player.getNickname(), gameItem.getName(), deskItem.getNumber(), position);
	}
	public synchronized void bootAssistant(long playerId, int position) {
		this.operatorVerfy();
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		PushBootAssistantCmd pushCmd = gameSeat.bootAssistant(player);
		//广播进入信息
		this.broadcast(pushCmd, playerId);
		logger.info("{}在席位:{}--{}--{}开启申请助手", player.getNickname(), gameItem.getName(), deskItem.getNumber(), position);

	}
	public synchronized void bootOnlooker(long playerId, int position) {
		this.operatorVerfy();
		Player player = getGameDesk().getPlayer(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		GameSeat gameSeat = getGameDesk().getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		PushBootOnlookerCmd pushCmd = gameSeat.bootOnlooker(player);
		//广播进入信息
		this.broadcast(pushCmd, playerId);
		logger.info("{}在席位:{}--{}--{}开启申请旁观者", player.getNickname(), gameItem.getName(), deskItem.getNumber(), position);
	}
	
	/***
	 * 设置(下一轮)主席位继任人
	 * @param player
	 */
	public final synchronized void setSeatSuccessor(long masterId, int position, long playerId) {
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
		
		PushSetSeatSuccessorCmd pushCmd = gameSeat.setSeatSuccessor(master, player);
		//广播进入信息
		this.broadcast(pushCmd, playerId);
		logger.info("{}在席位:{}--{}--{}将主席位继任者传给{}成功", player.getNickname(), gameItem.getName(), deskItem.getNumber(), position, playerId);
	}
	
	/***
	 * 强制席中的某人站起
	 * @param masterId
	 * @param playerId
	 */
	public synchronized void forceStandUp(long masterId, int position, long playerId) {
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
		
		PushStandupCmd pushCmd = gameSeat.forceStandUp(master, player);
		//广播进入信息
		this.broadcast(pushCmd, playerId);
		logger.info("{}在席位:{}--{}--{}请 {}站起", master.getNickname(), gameItem.getName(), deskItem.getNumber(), position, playerId);
	}
	
	/***
	 * 在席位中对某个席位进行打赏
	 * @param player
	 */
	public synchronized void reward(long playerId, List<Integer> positionList, Gift gift) {
		this.operatorVerfy();
		OutParam<Player> outParam = OutParam.build();
		List<PushRewardCmd> result = getGameDesk().reward(playerId, positionList, gift, outParam);
		for(PushRewardCmd pushCmd : result) {
			this.broadcast(pushCmd, playerId);
		}
		logger.info("{}对席位:{}--{}--{}打赏{}礼物", outParam.getParam().getNickname(), gameItem.getName(), deskItem.getNumber(), positionList, gift.getKind());
	}
	
	
	/***聊天****/
	public void chat(long playerId, List<Integer> positionList, Chat message) {
		this.operatorVerfy();
		OutParam<Player> outParam = OutParam.build();
		List<PushChatCmd> result = getGameDesk().chat(playerId, positionList, message, outParam);
		for(PushChatCmd pushCmd : result) {
			this.broadcast(pushCmd, playerId);
		}
		logger.info("{}在游戏桌:{}--{},对席位{}聊天{}", outParam.getParam().getNickname(), gameItem.getName(), deskItem.getNumber(), positionList, message.getKind());
	}
	

	/***申请直播****/
	public void applyBroadcastLive(long playerId) {
		this.operatorVerfy();
		OutParam<SeatPlayer> outParam = OutParam.build();
		PushApplyBroadcastLiveCmd result = getGameDesk().applyBroadcastLive(playerId, outParam);
		if(result != null) {
			this.broadcast(result, playerId);
		}
		logger.info("{}申请直播席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), deskItem.getNumber(), outParam.getParam().getGameSeat().getPosition());
	}
	/***取消直播****/
	public void cancleBroadcastLive(long playerId) {
		this.operatorVerfy();
		OutParam<SeatPlayer> outParam = OutParam.build();
		PushCancelBroadcastLiveCmd result = getGameDesk().cancelBroadcastLive(playerId, outParam);
		if(result != null) {
			this.broadcast(result, playerId);
		}
		logger.info("{}申请直播席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), deskItem.getNumber(), outParam.getParam().getGameSeat().getPosition());
	}
	/***直播****/
	public void broadcastLive(long playerId, byte[] data) {
		this.operatorVerfy();
		OutParam<SeatPlayer> outParam = OutParam.build();
		PushBroadcastLiveCmd pushCmd = getGameDesk().broadcastLive(playerId, data, outParam);
		this.broadcast(pushCmd, playerId);
		logger.info("{}正在直播:席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), deskItem.getNumber(), outParam.getParam().getGameSeat().getPosition());
	}
	/***
	 * 管理员同意XXX的直播
	 * @param player
	 * @param id
	 */
	public void approveBroadcastLive(long managerId, int position) {
		this.operatorVerfy();
		OutParam<Player> outParam = OutParam.build();
		PushApproveBroadcastLiveCmd pushCmd = getGameDesk().approveBroadcastLive(managerId, position, outParam);
		this.broadcast(pushCmd, managerId);
		logger.info("{}同意席位:{}--{}--{}的的直播", outParam.getParam().getNickname(), gameItem.getName(), deskItem.getNumber(), position);
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
			logger.info("{}在游戏桌:{}--{},申请管理员", outParam.getParam().getNickname(), gameItem.getName(), deskItem.getNumber());
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
		logger.info("{}在游戏桌:{}--{},将管理员之位传给:{}", outParam.getParam().getNickname(), gameItem.getName(), deskItem.getNumber(), playerId);
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
			logger.info("强制在席位:{}--{}--{},将{}踢走", gameItem.getName(), deskItem.getNumber(), seatPlayer.getGameSeat().getPosition(), seatPlayer.getPlayer().getNickname());
		}
	}
	
	public TableDesk getGameDesk() {
		return (TableDesk)baseDesk;
	}
}
