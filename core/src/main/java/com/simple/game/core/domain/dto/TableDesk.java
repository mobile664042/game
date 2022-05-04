package com.simple.game.core.domain.dto;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.push.game.notify.PushNotifyApplyManagerCmd;
import com.simple.game.core.domain.dto.constant.SeatPost;
import com.simple.game.core.domain.ext.Chat;
import com.simple.game.core.domain.ext.Gift;
import com.simple.game.core.domain.good.TableGame;
import com.simple.game.core.exception.BizException;

import lombok.ToString;

/***
 * 游戏桌
 * 
 * 游戏附加的一些核心玩法
 * 
 * @author zhibozhang
 *
 */
@ToString
public class TableDesk extends BaseDesk{
	private static Logger logger = LoggerFactory.getLogger(TableDesk.class);
	/****管理员***/
	protected final AtomicReference<Player> manager = new AtomicReference<Player>();
	
	/***
	 * 正在进行席位
	 * key position,席位
	 */
	protected final ConcurrentHashMap<Integer, GameSeat> seatPlayingMap = new ConcurrentHashMap<Integer, GameSeat>();
	
	
	public TableDesk(TableGame game) {
		super(game);
		for(int position = currentGame.getDeskItem().getMinPosition(); position <= currentGame.getDeskItem().getMinPosition(); position++) {
			GameSeat gameSeat = buildGameSeat(position); 
			seatPlayingMap.put(position, gameSeat);
		}
	} 
	
	
	/***
	 *聊天
	 */
	@Override
	public void chat(long playerId, Chat message, OutParam<Player> outParam) {
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("非法请求，不在游戏桌中"));
		}
		outParam.setParam(player);
		
		//TODO 需判断扣款
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
	public Player getPlayer(long playerId) {
		return playerMap.get(playerId);
	}
	protected GameSeat buildGameSeat(int position){
		return new GameSeat(this, position);
	}
	
	public void reward(long playerId, List<Integer> positionList, Gift gift, OutParam<Player> outParam) {
		checkPosistion(positionList);
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("非法请求，不在游戏桌中"));
		}
		outParam.setParam(player);
		
		//TODO 需判断扣款
		//SeatPlayer seatPlayer = getSeatPlayer(playerId);
	}

	public void chat(long playerId, List<Integer> positionList, Chat message, OutParam<Player> outParam) {
		checkPosistion(positionList);
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("非法请求，不在游戏桌中"));
		}
		outParam.setParam(player);
		
		//TODO 需判断扣款
		//SeatPlayer seatPlayer = getSeatPlayer(playerId);
	}


	public boolean applyBroadcastLive(long playerId, OutParam<SeatPlayer> outParam) {
		SeatPlayer seatPlayer = this.getSeatPlayer(playerId);
		if(seatPlayer == null) {
			throw new BizException(String.format("%s不在席位上，不可以同意申请直播", playerId));
		}
		if(seatPlayer.getSeatPost() == SeatPost.onlooker) {
			throw new BizException(String.format("%s旁观人员，不可以同意申请直播", playerId));
		}
		if(manager.get() == null) {
			throw new BizException(String.format("没有管理员，不可以同意申请直播"));
		}
		
		outParam.setParam(seatPlayer);
		if(seatPlayer.getGameSeat().isBroadcasting()) {
			throw new BizException(String.format("该主席位已经是直播了，不可以再申请直播！！"));
		}
		
		if(manager.get().getId() == playerId) {
			seatPlayer.getGameSeat().broadcasting = true;
			seatPlayer.getGameSeat().applyBroadcasted = false ;
			return true;
		}
		
		PushNotifyApplyManagerCmd pushCmd = new PushNotifyApplyManagerCmd();
		pushCmd.setPlayerId(playerId);
		pushCmd.setNickname(seatPlayer.getPlayer().getNickname());
		pushCmd.setHeadPic(seatPlayer.getPlayer().getHeadPic());
		manager.get().getOnline().push(pushCmd);
		logger.info("{}向管理员{}发送主播申请", seatPlayer.getPlayer().getNickname(), manager.get().getNickname());
		return false;
	}
	
	public void cancelBroadcastLive(long playerId, OutParam<SeatPlayer> outParam) {
		SeatPlayer seatPlayer = this.getSeatPlayer(playerId);
		if(seatPlayer == null) {
			throw new BizException(String.format("%s不在席位上，不可以同意申请直播", playerId));
		}
		if(seatPlayer.getSeatPost() == SeatPost.onlooker) {
			throw new BizException(String.format("%s旁观人员，不可以取消直播", playerId));
		}
		
		outParam.setParam(seatPlayer);
		if(!seatPlayer.getGameSeat().isBroadcasting()) {
			if(!seatPlayer.getGameSeat().isApplyBroadcasted()) {
				throw new BizException(String.format("该主席位不是直播，也没人申请直播！！"));
			}
		}
		
		seatPlayer.getGameSeat().broadcasting = false;
		seatPlayer.getGameSeat().applyBroadcasted = false;

		logger.info("{}已取消直播", seatPlayer.getPlayer().getNickname(), manager.get().getNickname());
	}
	
	

	public void approveBroadcastLive(long managerId, int position, OutParam<Player> outParam) {
		if(manager.get() == null) {
			throw new BizException(String.format("还没设置管理员"));
		}
		if(manager.get().getId() != managerId) {
			throw new BizException(String.format("%s不是管理员", managerId));
		}

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
		outParam.setParam(manager.get());
	}

	public void broadcastLive(long playerId, OutParam<SeatPlayer> outParam) {
		SeatPlayer seatPlayer = this.getSeatPlayer(playerId);
		if(seatPlayer == null) {
			throw new BizException(String.format("%s不在席位上，不可以直播", playerId));
		}
		if(seatPlayer.getSeatPost() != SeatPost.master) {
			throw new BizException(String.format("%s不是主席位，不可以直播", playerId));
		}
		outParam.setParam(seatPlayer);
	}
	
	public boolean applyManager(long playerId, OutParam<Player> outParam) {
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
			return true;
		}

		throw new BizException(String.format("管理员之位已被%s强走了，请重试吧", manager.get().getNickname()));
	}

	public void changeManager(long managerId, Long playerId, OutParam<Player> outParam) {
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

	protected void checkPosistion(List<Integer> positionList) throws BizException{
		for(int position : positionList) {
			if(this.seatPlayingMap.containsKey(position)) {
				throw new BizException(String.format("%s席位没有人入座", position));
			}
		}
	}
	
	public Long getManagerId() {
		if(manager.get() == null) {
			return null;
		}
		return manager.get().getId();
	}


	public SeatPlayer getSeatPlayer(long playerId) {
		for(GameSeat gameSeat : this.seatPlayingMap.values()) {
			SeatPlayer seatPlayer = gameSeat.getSeatPlayer(playerId);
			if(seatPlayer != null) {
				return seatPlayer;
			}
		}
		return null;
	}


	@Override
	public void left(long playerId, OutParam<Player> out) {
		SeatPlayer seatPlayer = this.getSeatPlayer(playerId);
		if(seatPlayer != null) {
			seatPlayer.getGameSeat().standUp(seatPlayer.getPlayer());
		}
		super.left(playerId, out);
	}


}
