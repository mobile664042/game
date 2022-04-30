package com.simple.game.core.domain.dto;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
import com.simple.game.core.domain.cmd.push.PushLeftCmd;
import com.simple.game.core.domain.cmd.push.PushNotifyApplyAssistantCmd;
import com.simple.game.core.domain.cmd.push.PushNotifyApplyManagerCmd;
import com.simple.game.core.domain.cmd.push.PushRewardCmd;
import com.simple.game.core.domain.cmd.push.PushRobSeatMasterCmd;
import com.simple.game.core.domain.cmd.push.PushSitdownCmd;
import com.simple.game.core.domain.cmd.push.PushStandupCmd;
import com.simple.game.core.domain.cmd.push.PushStopAssistantCmd;
import com.simple.game.core.domain.cmd.push.PushStopOnlookerCmd;
import com.simple.game.core.domain.cmd.rtn.RtnSeatInfoListCmd;
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
	private Player manager;
	
	/***
	 * 正在进行席位
	 * key position,席位
	 */
	protected final ConcurrentHashMap<Integer, GameSeat> seatPlayingMap = new ConcurrentHashMap<Integer, GameSeat>();
	
	
	public TableDesk(TableGame game) {
		super(game);
	} 
	
	/***
	 *聊天
	 */
	@Override
	public PushChatCmd chat(long playerId, Chat message, OutParam<Player> outParam) {
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("非法请求，不在游戏桌中"));
		}
		outParam.setParam(player);
		
		//TODO 需判断扣款
		SeatPlayer seatPlayer = getSeatPlayer(playerId);
		if(seatPlayer != null) {
			return seatPlayer.toPushChatCmd(message);		
		}
		else {
			return player.toPushChatCmd(message);		
		}
	}

	public PushSitdownCmd sitdown(long playerId, int position, OutParam<SeatPlayer> outParam) {
		//判断是否是在游戏桌中
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		if(position > this.currentGame.getDeskItem().getMaxPosition()) {
			throw new BizException(String.format("席位号%s不存在", position));
		}
		
		synchronized (this) {
			//判断是否经坐下
			for(GameSeat tempSeat : this.seatPlayingMap.values()) {
				if(tempSeat.getMaster().getPlayer().getId() == playerId) {
					outParam.setParam(tempSeat.getMaster());
					throw new BizException(String.format("已经在主席位%s中，不可再坐下", tempSeat.getPosition()));
				}
				for(SeatPlayer seatPlayer : tempSeat.getAssistantList()) {
					if(seatPlayer.getPlayer().getId() == playerId) {
						outParam.setParam(seatPlayer);
						throw new BizException(String.format("已经在桌位%s中(助手)，不可再坐下", tempSeat.getPosition()));
					}
				}
				for(SeatPlayer seatPlayer : tempSeat.getOnlookerList()) {
					if(seatPlayer.getPlayer().getId() == playerId) {
						outParam.setParam(seatPlayer);
						throw new BizException(String.format("已经在桌位%s中(助手)，不可再坐下", tempSeat.getPosition()));
					}
				}
			}
			
			//直接成为主席位
			GameSeat gameSeat = seatPlayingMap.get(position);
			if(gameSeat == null) {
				gameSeat = buildGameSeat(position);
				SeatPlayer master = new SeatPlayer(player, gameSeat, SeatPost.master);
				gameSeat.setMaster(master);
				seatPlayingMap.put(position, gameSeat);
				outParam.setParam(master);
				return master.toPushSitdownCmd();
			}
			player.setAddress(gameSeat);
			
			//判断是否超过最大限度
			if(gameSeat.getOnlookerList().size()+1 > currentGame.getGameItem().getSeatMaxonlooker()) {
				throw new BizException(String.format("人员已挤不下去了(已有%s)", currentGame.getGameItem().getSeatMaxonlooker()));
			}
			if(gameSeat.isStopOnlooker()) {
				throw new BizException(String.format("已禁止旁观！！"));
			}
			SeatPlayer onlooker = new SeatPlayer(player, gameSeat, SeatPost.onlooker);
			gameSeat.getOnlookerList().add(onlooker);
			outParam.setParam(onlooker);
			return onlooker.toPushSitdownCmd();
		}
	}

	/***
	 * 获取座位的在线信息
	 * 此处不需要验证玩家的position是否与请求的position是否为同一个
	 * @param playerId
	 * @param position
	 * @return
	 */
	public RtnSeatInfoListCmd getRtnSeatInfoListCmd(long playerId, int position) {
		//TODO 
		return new RtnSeatInfoListCmd();
	}
	protected GameSeat buildGameSeat(int position){
		return new GameSeat(this, position);
	}
	
	public List<PushRewardCmd> reward(long playerId, List<Integer> positionList, Gift gift, OutParam<Player> outParam) {
		checkPosistion(positionList);
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("非法请求，不在游戏桌中"));
		}
		outParam.setParam(player);
		
		//TODO 需判断扣款
		SeatPlayer seatPlayer = getSeatPlayer(playerId);
		if(seatPlayer != null) {
			return seatPlayer.toPushRewardCmd(positionList, gift);		
		}
		else {
			return player.toPushRewardCmd(positionList, gift);		
		}
	}

	public List<PushChatCmd> chat(long playerId, List<Integer> positionList, Chat message, OutParam<Player> outParam) {
		checkPosistion(positionList);
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("非法请求，不在游戏桌中"));
		}
		outParam.setParam(player);
		
		//TODO 需判断扣款
		SeatPlayer seatPlayer = getSeatPlayer(playerId);
		if(seatPlayer != null) {
			return seatPlayer.toPushChatCmd(positionList, message);		
		}
		else {
			return player.toPushChatCmd(positionList, message);		
		}
	}


	public PushApplyBroadcastLiveCmd applyBroadcastLive(long playerId, OutParam<SeatPlayer> outParam) {
		SeatPlayer seatPlayer = this.getSeatPlayer(playerId);
		if(seatPlayer == null) {
			throw new BizException(String.format("%s不在席位上，不可以同意申请直播", playerId));
		}
		if(seatPlayer.getSeatPost() == SeatPost.onlooker) {
			throw new BizException(String.format("%s旁观人员，不可以同意申请直播", playerId));
		}
		
		outParam.setParam(seatPlayer);
		if(seatPlayer.getGameSeat().isBroadcasting()) {
			throw new BizException(String.format("该主席位已经是直播了，不可以再申请直播！！"));
		}
		
		if(manager.getId() == playerId) {
			seatPlayer.getGameSeat().setBroadcasting(true);
			seatPlayer.getGameSeat().setApplyBroadcasted(false);
			return seatPlayer.toPushApplyBroadcastLiveCmd();
		}

		PushApplyBroadcastLiveCmd pushCmd = seatPlayer.toPushApplyBroadcastLiveCmd();
		manager.getOnline().push(pushCmd);
		logger.info("{}向管理员{}发送主播申请", seatPlayer.getPlayer().getNickname(), manager.getNickname());
		return null;
	}
	
	public PushCancelBroadcastLiveCmd cancelBroadcastLive(long playerId, OutParam<SeatPlayer> outParam) {
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
		
		seatPlayer.getGameSeat().setBroadcasting(false);
		seatPlayer.getGameSeat().setApplyBroadcasted(false);

		logger.info("{}已取消直播", seatPlayer.getPlayer().getNickname(), manager.getNickname());
		return seatPlayer.toPushCancelBroadcastLiveCmd();
	}
	
	

	public PushApproveBroadcastLiveCmd approveBroadcastLive(long managerId, int position, OutParam<Player> outParam) {
		if(manager == null) {
			throw new BizException(String.format("还没设置管理员"));
		}
		if(manager.getId() != managerId) {
			throw new BizException(String.format("%s不是管理员", managerId));
		}

		outParam.setParam(manager);
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
		gameSeat.setBroadcasting(true);
		gameSeat.setApplyBroadcasted(false);
		
		return gameSeat.getMaster().toPushApproveBroadcastLiveCmd();
	}

	public PushBroadcastLiveCmd broadcastLive(long playerId, byte[] data, OutParam<SeatPlayer> outParam) {
		SeatPlayer seatPlayer = this.getSeatPlayer(playerId);
		if(seatPlayer == null) {
			throw new BizException(String.format("%s不在席位上，不可以直播", playerId));
		}
		if(seatPlayer.getSeatPost() != SeatPost.master) {
			throw new BizException(String.format("%s不是主席位，不可以直播", playerId));
		}
		outParam.setParam(seatPlayer);
		return seatPlayer.toPushBroadcastLiveCmd(data);
	}
	
	public PushApplyManagerCmd applyManager(long playerId, OutParam<Player> outParam) {
		//判断是否是在游戏桌中
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("非法请求，不在游戏桌中"));
		}
		
		outParam.setParam(player);
		if(manager != null) {
			if(manager.getId() == playerId) {
				throw new BizException(String.format("%s已是管理员, 不可再申请", playerId));
			}
			
			//向这管理员发送告知
			PushNotifyApplyManagerCmd pushCmd = player.toPushNotifyApplyManagerCmd();
			manager.getOnline().push(pushCmd);
			logger.info("{}向管理员{}发送更换管理员申请", player.getNickname(), manager.getNickname());
			return null;
		}

		manager = playerMap.get(playerId);
		return manager.toPushApplyManagerCmd();
	}

	public PushChangeManagerCmd changeManager(long managerId, Long playerId, OutParam<Player> outParam) {
		if(manager == null) {
			throw new BizException(String.format("还没设置管理员"));
		}
		if(manager.getId() != managerId) {
			throw new BizException(String.format("%s不是管理员", playerId));
		}
		
		outParam.setParam(manager);
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
			manager = player;
		}
		else {
			//取消管理员之位
			manager = null;
		}
		return outParam.getParam().toPushChangeManagerCmd(playerId);
	}

	protected void checkPosistion(List<Integer> positionList) throws BizException{
		for(int position : positionList) {
			if(this.seatPlayingMap.containsKey(position)) {
				throw new BizException(String.format("%s席位没有人入座", position));
			}
		}
	}
	
	public Long getManagerId() {
		if(manager == null) {
			return null;
		}
		return manager.getId();
	}


	public PushSitdownCmd quickSitdown(long playerId, OutParam<SeatPlayer> outParam) {
		Integer target = null;
		for(int position = 1; position <= this.currentGame.getDeskItem().getMaxPosition(); position++) {
			if(this.seatPlayingMap.containsKey(position)) {
				continue;
			}
			target = position;
			break;
		}
		if(target == null) {
			throw new BizException("已经没有空闲的席位了"); 
		}
		return this.sitdown(playerId, target, outParam);
	}

	public void applyAssistant(long playerId, OutParam<SeatPlayer> outParam) {
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		
		SeatPlayer target = this.getSeatPlayer(playerId);
		if(target == null) {
			throw new BizException(String.format("不在席位上，不可以申请辅助"));
		}
		if(target.getGameSeat().getMaster() == null) {
			throw new BizException(String.format("不存在主席位了不可以申请辅助"));
		}
		if(target.getGameSeat().getMaster().getPlayer().getId() == playerId) {
			throw new BizException(String.format("已经是主席位了不可以申请辅助"));
		}
		if(target.getSeatPost() == SeatPost.assistant) {
			throw new BizException(String.format("已经是辅助了不可以申请辅助"));
		}
		//判断是否不允许助手
		if(target.getGameSeat().isStopAssistant()) {
			throw new BizException(String.format("主席位设置不允许申请助手"));
		}
		
		outParam.setParam(target);
		target.setApplyAssistanted(true);
		
		//发送到主席位中去
		SeatPlayer master = target.getGameSeat().getMaster();
		PushNotifyApplyAssistantCmd pushCmd = target.toPushNotifyApplyAssistantCmd();
		master.getPlayer().getOnline().push(pushCmd);
		logger.info("{}向主席位{}发送辅助申请", target.getPlayer().getNickname(), master.getPlayer().getNickname());
	}
	

	public PushNotifyApplyAssistantCmd approveApplyAssistant(long masterId, long playerId, OutParam<SeatPlayer> outParam) {
		SeatPlayer master = this.getSeatPlayer(masterId);
		if(master == null) {
			throw new BizException(String.format("%s不在席位上，不可以同意申请辅助", masterId));
		}
		if(master.getSeatPost() != SeatPost.master) {
			throw new BizException(String.format("%s不在主席位上，不可以同意申请辅助", masterId));
		}
		
		SeatPlayer other = this.getSeatPlayer(playerId);
		if(other == null) {
			throw new BizException(String.format("%s不在席位上，不可以同意申请辅助", playerId));
		}
		if(other.getGameSeat().getPosition() != master.getGameSeat().getPosition()) {
			throw new BizException(String.format("%s与%s不在同一个席位，不可以同意申请辅助", masterId, playerId));
		}
		if(!other.isApplyAssistanted()) {
			throw new BizException(String.format("%s并没有申请辅助", playerId));
		}
		
		other.getGameSeat().getOnlookerList().remove(other);
		other.getGameSeat().getAssistantList().add(other);
		other.setSeatPost(SeatPost.assistant);
		other.setApplyAssistanted(false);
		outParam.setParam(master);
		
		return other.toPushNotifyApplyAssistantCmd();
	}

	public SeatPlayer getSeatPlayer(long playerId) {
		for(GameSeat gameSeat : this.seatPlayingMap.values()) {
			if(gameSeat.getMaster().getPlayer().getId() == playerId) {
				return gameSeat.getMaster();
			}
			for(SeatPlayer seatPlayer : gameSeat.getAssistantList()) {
				if(seatPlayer.getPlayer().getId() == playerId) {
					return seatPlayer;
				}	
			}
			for(SeatPlayer seatPlayer : gameSeat.getOnlookerList()) {
				if(seatPlayer.getPlayer().getId() == playerId) {
					return seatPlayer;
				}	
			}
		}
		return null;
	}


	/***操作验证***/
	protected SeatPlayer checkSeatMaster(long playerId) {
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		
		for(GameSeat gameSeat : seatPlayingMap.values()) {
			if(gameSeat.getMaster() != null && gameSeat.getMaster().getPlayer().getId() == playerId) {
				return gameSeat.getMaster();
			}
		}
		throw new BizException(String.format("非法操作，不是主席位不能操作"));
	}

	public PushStopOnlookerCmd stopOnlooker(long playerId, OutParam<SeatPlayer> outParam) {
		SeatPlayer player = checkSeatMaster(playerId);
		player.getGameSeat().setStopOnlooker(true);
		outParam.setParam(player);
		return player.toPushStopOnlookerCmd();
	}
	public PushStopAssistantCmd stopAssistant(long playerId, OutParam<SeatPlayer> outParam) {
		SeatPlayer player = checkSeatMaster(playerId);
		player.getGameSeat().setStopAssistant(true);
		
		outParam.setParam(player);
		return player.toPushStopAssistantCmd();
	}

	public PushBootOnlookerCmd bootOnlooker(long playerId, OutParam<SeatPlayer> outParam) {
		SeatPlayer player = checkSeatMaster(playerId);
		player.getGameSeat().setStopOnlooker(false);

		outParam.setParam(player);
		return player.toPushBootOnlookerCmd();
	}
	public PushBootAssistantCmd bootAssistant(long playerId, OutParam<SeatPlayer> outParam) {
		SeatPlayer player = checkSeatMaster(playerId);
		player.getGameSeat().setStopAssistant(false);
		outParam.setParam(player);
		return player.toPushBootAssistantCmd();
	}
	public SeatPlayer preStandUp(long masterId, long playerId) {
		if(masterId == playerId) {
			throw new BizException(String.format("%s不可以对自己使用强制站起", playerId));
		}
		SeatPlayer master = checkSeatMaster(masterId);
		SeatPlayer other = this.getSeatPlayer(playerId);
		if(other == null) {
			throw new BizException(String.format("%s不在席位上，不可以强制使他站起", playerId));
		}
		if(other.getGameSeat().getPosition() != master.getGameSeat().getPosition()) {
			throw new BizException(String.format("%s与%s不在同一个席位，不可以强制使他站起", masterId, playerId));
		}
		return master;
	}
	public PushRobSeatMasterCmd robSeatMaster(long playerId, OutParam<SeatPlayer> outParam) {
		SeatPlayer target = this.getSeatPlayer(playerId);
		if(target == null) {
			throw new BizException(String.format("%s不在席位上，不可以申请主席位", playerId));
		}
		if(target.getSeatPost() == SeatPost.master) {
			throw new BizException(String.format("%s已经是主席位，不可以申请主席位", playerId));
		}
		if(target.getGameSeat().getMaster() != null) {
			throw new BizException(String.format("主席位已经有人了，不可以申请主席位"));
		}
		
		if(target.getSeatPost() == SeatPost.assistant) {
			target.getGameSeat().getAssistantList().remove(target);
			target.getGameSeat().setMaster(target);
		}
		else if(target.getSeatPost() == SeatPost.onlooker) {
			target.getGameSeat().getOnlookerList().remove(target);
			target.getGameSeat().setMaster(target);
		} 
		// 与 standUp(对应)
		return target.toPushRobSeatMasterCmd();
	}
	
	public PushStandupCmd standUp(long playerId, OutParam<SeatPlayer> outParam) {
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		
		for(GameSeat gameSeat : seatPlayingMap.values()) {
			if(gameSeat.getMaster() != null && gameSeat.getMaster().getPlayer().getId() == playerId) {
				outParam.setParam(gameSeat.getMaster());
				gameSeat.setMaster(null);
				if(gameSeat.getAssistantList().size() == 0 || gameSeat.getOnlookerList().size() == 0) {
					seatPlayingMap.remove(gameSeat.getPosition());
				}
				player.setAddress(this);
				return outParam.getParam().toPushStandupCmd();
			}
			for(SeatPlayer seatPlayer : gameSeat.getAssistantList()) {
				if(seatPlayer.getPlayer().getId() == playerId) {
					gameSeat.getAssistantList().remove(seatPlayer);
					outParam.setParam(seatPlayer);
					player.setAddress(this);
					return seatPlayer.toPushStandupCmd();
				}
			}
			for(SeatPlayer seatPlayer : gameSeat.getOnlookerList()) {
				if(seatPlayer.getPlayer().getId() == playerId) {
					gameSeat.getOnlookerList().remove(seatPlayer);
					outParam.setParam(seatPlayer);
					player.setAddress(this);
					return seatPlayer.toPushStandupCmd();
				}
			}
		}
		
		throw new BizException(String.format("%s不在桌位中", playerId));
	}

	@Override
	public PushLeftCmd left(long playerId, OutParam<Player> out) {
		SeatPlayer seatPlayer = this.getSeatPlayer(playerId);
		if(seatPlayer != null) {
			this.standUp(playerId, OutParam.build());
		}
		return super.left(playerId, out);
	}

	public ConcurrentHashMap<Integer, GameSeat> getSeatPlayingMap() {
		return seatPlayingMap;
	}


}
