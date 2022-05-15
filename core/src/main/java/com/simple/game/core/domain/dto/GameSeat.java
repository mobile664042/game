package com.simple.game.core.domain.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.game.notify.PushNotifyApplySeatSuccessorCmd;
import com.simple.game.core.domain.cmd.push.game.notify.PushNotifyChangeSeatMasterCmd;
import com.simple.game.core.domain.cmd.push.seat.PushStandUpCmd;
import com.simple.game.core.domain.cmd.push.seat.notify.PushNotifyApplyAssistantCmd;
import com.simple.game.core.domain.cmd.rtn.seat.RtnGameSeatInfoCmd;
import com.simple.game.core.domain.dto.constant.SeatPost;
import com.simple.game.core.exception.BizException;
import com.simple.game.core.util.SimpleUtil;

import lombok.Getter;
import lombok.ToString;

/***
 * 游戏席位
 * 
 * @author zhibozhang
 *
 */
//@Data
@Getter
@ToString
public class GameSeat implements AddressNo{
	private static Logger logger = LoggerFactory.getLogger(GameSeat.class);

	/***所属的桌子****/
	protected final TableDesk desk;
	
	/***席位号从1号开始****/
	protected final int position;
	
//	/***扩展属性****/
//	private Object extConfig;
	
	/***席位主要人员****/
	protected final AtomicReference<SeatPlayer> master = new AtomicReference<SeatPlayer>();
	
	/***(下一轮)主席位继任者****/
	protected SeatPlayer nextMaster;
	
	/***一个席位可以有多个助手人员****/
	protected final ConcurrentHashMap<Long, SeatPlayer> assistantMap = new ConcurrentHashMap<Long, SeatPlayer>();
	
	/***
	 * 进入的玩家
	 * 会有很多旁观人群
	 * key playerId
	 */
	protected final ConcurrentHashMap<Long, SeatPlayer> seatPlayerMap = new ConcurrentHashMap<Long, SeatPlayer>();
	
	
	protected boolean stopOnlooker = false;
	protected boolean stopAssistant = false;
	
	/***是否审核通过直播***/
	protected boolean broadcasting = false;
	
	/***是否申请直播***/
	protected boolean applyBroadcasted = false;
	

	public GameSeat(TableDesk desk, int position) {
		this.desk = desk;
		this.position = position;
	}
	
	public String getAddrNo() {
		return desk.getAddrNo() + "@" + position;
	}
	
	public boolean isIdle(){
		return this.getMaster().get() == null;
	}
	
	public void handleChangeMaster() {
		if(nextMaster == null) {
			return;
		}
		
		try {
			//判断游戏币是否足够
			this.preSitdown(nextMaster.player);
			master.set(nextMaster);
			nextMaster = null;
			
			PushNotifyChangeSeatMasterCmd pushCmd = new PushNotifyChangeSeatMasterCmd();
			pushCmd.setPlayerId(master.get().getPlayer().getId());
			pushCmd.setNickname(master.get().getPlayer().getNickname());
			pushCmd.setHeadPic(master.get().getPlayer().getHeadPic());
			this.getDesk().getTableGame().broadcast(pushCmd, true);
			logger.info("{}的主席位由{}担任", this.position, master.get().player.getId());
		}
		catch(BizException e) {
			SeatPlayer old = nextMaster;
			nextMaster = null;
			
			PushNotifyChangeSeatMasterCmd pushCmd = new PushNotifyChangeSeatMasterCmd();
			pushCmd.setPlayerId(old.getPlayer().getId());
			pushCmd.setNickname(old.getPlayer().getNickname());
			pushCmd.setHeadPic(old.getPlayer().getHeadPic());
			this.getDesk().getTableGame().broadcast(pushCmd, true);
			logger.warn("playerId={}, 不满足{}主席位重新坐下条件！", old.player.getId(), this.position, e);
			
		}
	}
	
	public final void broadcast(PushCmd cmd, long ...excludeIds){
		logger.info("cmd={}, 接收到推送信息！", cmd.toLogStr());
		long startTime = System.currentTimeMillis();
		for(SeatPlayer player : seatPlayerMap.values()) {
			if(!SimpleUtil.contain(player.getPlayer().getId(), excludeIds)) {
				player.getPlayer().getOnline().push(cmd);
			}
		}
		logger.info("cmd={}, 推送完成, 耗时:{}毫秒！", cmd.toLogStr(), System.currentTimeMillis() - startTime);
	}
	
	public void standupAll() {
		if(this.master.get() == null) {
			logger.warn("主席位是空的，不需要站起，是不是产生bug了？");
			return;
		}
		
		standUp(this.master.get().getPlayer(), true);
	}
	
	protected void preSitdown(Player player) {}
	
	public SeatPlayer sitdown(Player player) {
		preSitdown(player);
		//判断是否经坐下
		SeatPlayer old = seatPlayerMap.get(player.getId());
		if(old != null) {
			throw new BizException(String.format("已经在桌位%s中，不可再坐下", old.getGameSeat().getPosition()));
		}
		
		SeatPlayer seatPlayer = null;
		//直接成为主席位
		if(this.master.get() == null) {
			seatPlayer = buildSeatPlayer(player, SeatPost.master);
			if(this.master.compareAndSet(null, seatPlayer)) {
				doSitdownMaster();
			}
			else {
				throw new BizException(String.format("主席之位已被%s抢走了，请重试吧", master.get().getPlayer().getNickname()));
			}
		}
		else {
			//判断是否超过最大限度，不需要加锁判断，减少时死锁，提高性能，允许极少量的误差
			if(seatPlayerMap.size() >= desk.getTableGame().getGameItem().getSeatMaxFans()) {
				throw new BizException(String.format("人员已挤不下去了(已有%s)", desk.getTableGame().getGameItem().getSeatMaxFans()));
			}
			if(this.isStopOnlooker()) {
				throw new BizException(String.format("已禁止旁观！！"));
			}
			seatPlayer = buildSeatPlayer(player, SeatPost.onlooker);
		}
		
		
		
		seatPlayerMap.put(player.getId(), seatPlayer);
		player.setAddress(this);
		
		return seatPlayer;
	}
	
	protected void doSitdownMaster() {
	}
	
	/***
	 * 获取座位的在线信息
	 * 此处不需要验证玩家的position是否与请求的position是否为同一个
	 * @param playerId
	 * @param position
	 * @return
	 */
	public List<SeatPlayer> getSeatPlayerList() {
		return new ArrayList<SeatPlayer>(seatPlayerMap.values());
	}
	public List<SeatPlayer> getAssistantList() {
		return new ArrayList<SeatPlayer>(assistantMap.values());
	}
	protected SeatPlayer buildSeatPlayer(Player player, SeatPost seatPost){
		return new SeatPlayer(player, this, seatPost);
	}
	

	/***
	 * 申请成为助手
	 * @param player
	 */
	public void applyAssistant(Player player) {
		SeatPlayer target = this.seatPlayerMap.get(player.getId());
		if(target == null) {
			throw new BizException(String.format("不在席位上，不可以申请辅助"));
		}
		if(target.getGameSeat().getMaster().get() == null) {
			throw new BizException(String.format("不存在主席位了不可以申请辅助"));
		}
		if(target.getGameSeat().getMaster().get().getPlayer().getId() == player.getId()) {
			throw new BizException(String.format("已经是主席位了不可以申请辅助"));
		}
		if(target.getSeatPost() == SeatPost.assistant) {
			throw new BizException(String.format("已经是辅助了不可以申请辅助"));
		}
		//判断是否不允许助手
		if(target.getGameSeat().isStopAssistant()) {
			throw new BizException(String.format("主席位设置不允许申请助手"));
		}
		
		target.applyAssistanted = true;
		
		//发送到主席位中去
		SeatPlayer master = target.getGameSeat().getMaster().get();
		PushNotifyApplyAssistantCmd pushCmd = new PushNotifyApplyAssistantCmd();
		pushCmd.setPlayerId(position);
		pushCmd.setNickname(player.getNickname());
		pushCmd.setHeadPic(player.getHeadPic());
		master.getPlayer().getOnline().push(pushCmd);
		logger.info("{}向主席位{}发送辅助申请", target.getPlayer().getNickname(), master.getPlayer().getNickname());
	}
	

	public void approveApplyAssistant(Player master, Player player) {
		SeatPlayer seartMaster = checkSeatMaster(master.getId());

		SeatPlayer other = this.seatPlayerMap.get(player.getId());
		if(other == null) {
			throw new BizException(String.format("%s不在席位上，不可以同意申请辅助", player.getId()));
		}
		if(other.getGameSeat().getPosition() != seartMaster.getGameSeat().getPosition()) {
			throw new BizException(String.format("%s与%s不在同一个席位，不可以同意申请辅助", master.getId(), player.getId()));
		}
		if(!other.isApplyAssistanted()) {
			throw new BizException(String.format("%s并没有申请辅助", player.getId()));
		}
		
		other.getGameSeat().assistantMap.put(other.getPlayer().getId(), other);
		other.seatPost = SeatPost.assistant;
		other.applyAssistanted = false;
		
//		return other.toPushNotifyApplyAssistantCmd();
	}


	/***操作验证***/
	protected SeatPlayer checkSeatMaster(long playerId) {
		SeatPlayer seartMaster = this.seatPlayerMap.get(playerId);
		if(seartMaster == null) {
			throw new BizException(String.format("%s不在席位上，不可以进行管理操作", playerId));
		}
		if(seartMaster.getSeatPost() != SeatPost.master) {
			throw new BizException(String.format("%s不在主席位上，不可以进行管理操作", playerId));
		}
		return seartMaster;
	}

	public void stopOnlooker(Player player) {
		SeatPlayer seartMaster = checkSeatMaster(player.getId());
		seartMaster.getGameSeat().stopOnlooker = true;
	}
	public void stopAssistant(Player player) {
		SeatPlayer seartMaster = checkSeatMaster(player.getId());
		seartMaster.getGameSeat().stopAssistant = true;
	}

	public void bootOnlooker(Player player) {
		SeatPlayer seartMaster = checkSeatMaster(player.getId());
		seartMaster.getGameSeat().stopOnlooker = false;
	}
	public void bootAssistant(Player player) {
		SeatPlayer seartMaster = checkSeatMaster(player.getId());
		seartMaster.getGameSeat().stopAssistant = false;
	}
	public SeatPlayer preStandUp(long masterId, long playerId) {
		if(masterId == playerId) {
			throw new BizException(String.format("%s不可以对自己使用强制站起", playerId));
		}
		SeatPlayer master = checkSeatMaster(masterId);
		SeatPlayer other = this.seatPlayerMap.get(playerId);
		if(other == null) {
			throw new BizException(String.format("%s与%s不在同一个席位，不可以同意申请辅助", masterId, playerId));
		}
		return master;
	}

	public void applySeatSuccessor(Player player) {
		if(player.getId() == this.master.get().getPlayer().getId()) {
			throw new BizException(String.format("你已是主席位"));
		}
		
		SeatPlayer other = this.seatPlayerMap.get(player.getId());
		if(other == null) {
			throw new BizException(String.format("%s不在席位上，不可以申请席位继任者", player.getId()));
		}
		
		//向主席位发送告知
		PushNotifyApplySeatSuccessorCmd pushCmd = new PushNotifyApplySeatSuccessorCmd();
		pushCmd.setPlayerId(player.getId());
		pushCmd.setHeadPic(player.getHeadPic());
		pushCmd.setNickname(player.getNickname());
		master.get().getPlayer().getOnline().push(pushCmd);
		logger.info("{}向主席位{}发送更换管理员申请", player.getNickname(), master.get().getPlayer().getNickname());
	}
	
	public void setSeatSuccessor(Player master, Player player) {
		SeatPlayer seartMaster = checkSeatMaster(master.getId());

		SeatPlayer other = this.seatPlayerMap.get(player.getId());
		if(other == null) {
			throw new BizException(String.format("%s不在席位上，不可以设置席位继认者", player.getId()));
		}
		if(other.getGameSeat().getPosition() != seartMaster.getGameSeat().getPosition()) {
			throw new BizException(String.format("%s与%s不在同一个席位，不可以设置席位继认者", master.getId(), player.getId()));
		}
		if(!other.isApplyAssistanted()) {
			throw new BizException(String.format("%s不是辅助，不可以成为席位继认者", player.getId()));
		}
		
		this.nextMaster = other;
	}
	
	/***
	 * 主席位离开了
	 * @param master
	 * @return
	 */
	protected void doStandUpMaster(boolean isSys, SeatPlayer seatPlayer) {
		if(!isSys) {
			return;
		}
		//给其他人发广播
		PushStandUpCmd pushCmd = new PushStandUpCmd();
		pushCmd.setPlayerId(seatPlayer.getPlayer().getId());
		pushCmd.setNickname(seatPlayer.getPlayer().getNickname());
		pushCmd.setHeadPic(seatPlayer.getPlayer().getHeadPic());
		pushCmd.setSeatPost(seatPlayer.getSeatPost());
		pushCmd.setPosition(position);
		//发送广播
		seatPlayer.getGameSeat().getDesk().getTableGame().broadcast(pushCmd);
	}
	
	public void standUp(Player player, boolean isSys) {
		SeatPlayer seatPlayer = this.seatPlayerMap.get(player.getId());
		if(seatPlayer == null) {
			throw new BizException(String.format("%s不在席位上，不可以站起", player.getId()));
		}
		
		if(seatPlayer.getSeatPost() == SeatPost.master) {
			//主席位站起, 全部清空
			doStandUpMaster(isSys, seatPlayer);
			this.clear();
			master.set(null);
			
			//广播
			logger.info("{}席位的全体同仁都站起来了", this.position);
		}
		else if(seatPlayer.getSeatPost() == SeatPost.assistant) {
			//主席位站起
			assistantMap.remove(player.getId());
			seatPlayerMap.remove(player.getId());
		}
		else {
			seatPlayerMap.remove(player.getId());
		}
	}
	
	private void clear() {
		seatPlayerMap.clear();
		assistantMap.clear();
		this.master.set(null);
		this.nextMaster = null;
		
		this.stopOnlooker = false;
		this.stopAssistant = false;
		this.broadcasting = false;
		this.applyBroadcasted = false;
	}

	public void forceStandUp(Player master, Player player) {
		SeatPlayer seartMaster = checkSeatMaster(master.getId());

		SeatPlayer other = this.seatPlayerMap.get(player.getId());
		if(other == null) {
			throw new BizException(String.format("%s不在席位上，不可以强制粉丝站起", player.getId()));
		}
		if(other.getGameSeat().getPosition() != seartMaster.getGameSeat().getPosition()) {
			throw new BizException(String.format("%s与%s不在同一个席位，不可以强制粉丝站起", master.getId(), player.getId()));
		}
		if(master.getId() == player.getId()) {
			throw new BizException(String.format("不可以对自己进行强制站起"));
		}
		
		this.standUp(player, false);
	}
	
	public RtnGameSeatInfoCmd getGameSeatInfo() {
		RtnGameSeatInfoCmd rtnCmd = new RtnGameSeatInfoCmd();
		if(master.get() != null) {
			rtnCmd.setMaster(master.get().player.valueOfPlayerVo());
		}
		if(nextMaster != null) {
			rtnCmd.setNextMaster(nextMaster.player.valueOfPlayerVo());
		}
		rtnCmd.setPosition(position);
		rtnCmd.setStopOnlooker(stopOnlooker);
		rtnCmd.setStopAssistant(stopAssistant);
		rtnCmd.setBroadcasting(broadcasting);
		rtnCmd.setApplyBroadcasted(applyBroadcasted);
		return rtnCmd;
	}
	
	
	public SeatPlayer getSeatPlayer(long playerId) {
		return seatPlayerMap.get(playerId);
	}
	

	/***
	 * 获取粉丝数
	 * @return
	 */
	public int getFansCount() {
		return seatPlayerMap.size();
	}

	public void setStopOnlooker(boolean stopOnlooker) {
		this.stopOnlooker = stopOnlooker;
	}

	public void setStopAssistant(boolean stopAssistant) {
		this.stopAssistant = stopAssistant;
	}

}
