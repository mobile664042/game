package com.simple.game.core.domain.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.push.PushBootAssistantCmd;
import com.simple.game.core.domain.cmd.push.PushBootOnlookerCmd;
import com.simple.game.core.domain.cmd.push.PushCancelSeatSuccessorCmd;
import com.simple.game.core.domain.cmd.push.PushChangeSeatMasterCmd;
import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.PushNotifyApplyAssistantCmd;
import com.simple.game.core.domain.cmd.push.PushSetSeatSuccessorCmd;
import com.simple.game.core.domain.cmd.push.PushSitdownCmd;
import com.simple.game.core.domain.cmd.push.PushStandupCmd;
import com.simple.game.core.domain.cmd.push.PushStopAssistantCmd;
import com.simple.game.core.domain.cmd.push.PushStopOnlookerCmd;
import com.simple.game.core.domain.cmd.rtn.RtnGameSeatCmd;
import com.simple.game.core.domain.cmd.rtn.RtnSeatInfoListCmd;
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
	protected TableDesk desk;
	
	/***席位号从1号开始****/
	protected int position;
	
//	/***扩展属性****/
//	private Object extConfig;
	
	/***席位主要人员****/
	protected SeatPlayer master;
	
	/***(下一轮)主席位继任者****/
	protected SeatPlayer nextMaster;
	
	/***一个席位可以有多个助手人员****/
	protected final List<SeatPlayer> assistantList = new ArrayList<SeatPlayer>();
	
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
	
	public int getAddrNo() {
		return position;
	}
	
	public boolean isIdle(){
		return this.master == null;
	}
	
	public void handleChangeMaster() {
		if(nextMaster == null) {
			return;
		}
		
		try {
			//判断游戏币是否足够
			this.preSitdown(nextMaster.player);
			PushChangeSeatMasterCmd pushCmd = this.toPushChangeSeatMasterCmd();
			this.getDesk().broadcast(pushCmd);
			logger.info("{}的主席位由{}担任", this.position, nextMaster.player.getId());
			nextMaster = null;
		}
		catch(BizException e) {
			logger.warn("playerId={}, 不满足{}主席位重新坐下条件！", nextMaster.player.getId(), this.position, e);
			nextMaster = null;
			
			PushCancelSeatSuccessorCmd pushCmd = this.toPushCancelSeatSuccessorCmd();
			this.getDesk().broadcast(pushCmd);
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
		standUp(this.master.player);
	}
	
	protected void preSitdown(Player player) {}
	
	public PushSitdownCmd sitdown(Player player) {
		preSitdown(player);
		synchronized (this) {
			//判断是否经坐下
			SeatPlayer old = seatPlayerMap.get(player.getId());
			if(old != null) {
				throw new BizException(String.format("已经在桌位%s中，不可再坐下", old.getGameSeat().getPosition()));
			}
			
			SeatPlayer seatPlayer = null;
			//直接成为主席位
			if(this.master == null) {
				seatPlayer = buildSeatPlayer(player, SeatPost.master);
				this.master = seatPlayer;
				doSitdownMaster();
			}
			else {
				//判断是否超过最大限度
				if(seatPlayerMap.size() >= desk.getCurrentGame().getGameItem().getSeatMaxFans()) {
					throw new BizException(String.format("人员已挤不下去了(已有%s)", desk.getCurrentGame().getGameItem().getSeatMaxFans()));
				}
				if(this.isStopOnlooker()) {
					throw new BizException(String.format("已禁止旁观！！"));
				}
				seatPlayer = buildSeatPlayer(player, SeatPost.onlooker);
			}
			
			seatPlayerMap.put(player.getId(), seatPlayer);
			player.setAddress(this);
			return seatPlayer.toPushSitdownCmd();
		}
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
	public RtnSeatInfoListCmd getRtnSeatInfoListCmd() {
		//TODO 
		return new RtnSeatInfoListCmd();
	}
	protected SeatPlayer buildSeatPlayer(Player player, SeatPost seatPost){
		return new SeatPlayer(player, this, seatPost);
	}
	


	public void applyAssistant(Player player) {
		SeatPlayer target = this.seatPlayerMap.get(player.getId());
		if(target == null) {
			throw new BizException(String.format("不在席位上，不可以申请辅助"));
		}
		if(target.getGameSeat().getMaster() == null) {
			throw new BizException(String.format("不存在主席位了不可以申请辅助"));
		}
		if(target.getGameSeat().getMaster().getPlayer().getId() == player.getId()) {
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
		SeatPlayer master = target.getGameSeat().getMaster();
		PushNotifyApplyAssistantCmd pushCmd = target.toPushNotifyApplyAssistantCmd();
		master.getPlayer().getOnline().push(pushCmd);
		logger.info("{}向主席位{}发送辅助申请", target.getPlayer().getNickname(), master.getPlayer().getNickname());
	}
	

	public PushNotifyApplyAssistantCmd approveApplyAssistant(Player master, Player player) {
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
		
		other.getGameSeat().getAssistantList().add(other);
		other.seatPost = SeatPost.assistant;
		other.applyAssistanted = false;
		
		return other.toPushNotifyApplyAssistantCmd();
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

	public PushStopOnlookerCmd stopOnlooker(Player player) {
		SeatPlayer seartMaster = checkSeatMaster(player.getId());
		seartMaster.getGameSeat().stopOnlooker = true;
		return seartMaster.toPushStopOnlookerCmd();
	}
	public PushStopAssistantCmd stopAssistant(Player player) {
		SeatPlayer seartMaster = checkSeatMaster(player.getId());
		seartMaster.getGameSeat().stopAssistant = true;
		return seartMaster.toPushStopAssistantCmd();
	}

	public PushBootOnlookerCmd bootOnlooker(Player player) {
		SeatPlayer seartMaster = checkSeatMaster(player.getId());
		seartMaster.getGameSeat().stopOnlooker = false;
		return seartMaster.toPushBootOnlookerCmd();
	}
	public PushBootAssistantCmd bootAssistant(Player player) {
		SeatPlayer seartMaster = checkSeatMaster(player.getId());
		seartMaster.getGameSeat().stopAssistant = false;
		return seartMaster.toPushBootAssistantCmd();
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
	public PushSetSeatSuccessorCmd setSeatSuccessor(Player master, Player player) {
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

		// 与 standUp(对应)
		return this.toPushSetSeatSuccessorCmd();
	}
	
	/***
	 * 主席位离开了
	 * @param master
	 * @return
	 */
	protected void doStandUpMaster() {
	}
	
	public PushStandupCmd standUp(Player player) {
		SeatPlayer seatPlayer = this.seatPlayerMap.get(player.getId());
		if(seatPlayer == null) {
			throw new BizException(String.format("%s不在席位上，不可以站起", player.getId()));
		}
		
		if(seatPlayer.getSeatPost() == SeatPost.master) {
			//主席位站起, 全部清空
			doStandUpMaster();
			this.clear();
			
			//广播
			PushStandupCmd pushCmd = this.toPushStandupCmd();
			this.desk.getCurrentGame().broadcast(pushCmd, player.getId());
			logger.info("{}席位的全体同仁都站起来了");
		}
		else if(seatPlayer.getSeatPost() == SeatPost.assistant) {
			//主席位站起
			assistantList.remove(seatPlayer);
			seatPlayerMap.remove(player.getId());
		}
		else {
			seatPlayerMap.remove(player.getId());
		}
		return seatPlayer.toPushStandupCmd();
	}
	
	private void clear() {
		seatPlayerMap.clear();
		assistantList.clear();
		this.master = null;
		this.nextMaster = null;
		
		this.stopOnlooker = false;
		this.stopAssistant = false;
		this.broadcasting = false;
		this.applyBroadcasted = false;
	}

	public PushStandupCmd forceStandUp(Player master, Player player) {
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
		
		return this.standUp(player);
	}
	
	public PushSetSeatSuccessorCmd toPushSetSeatSuccessorCmd() {
		//TODO 
		return null;
	}
	
	public RtnGameSeatCmd getRtnGameSeatCmd() {
		//TODO 
		return new RtnGameSeatCmd();
	}
	
	public PushStandupCmd toPushStandupCmd() {
		//TODO 
		return new PushStandupCmd();
	}
	
	public PushCancelSeatSuccessorCmd toPushCancelSeatSuccessorCmd() {
		//TODO 
		return new PushCancelSeatSuccessorCmd();
	}
	
	public PushChangeSeatMasterCmd toPushChangeSeatMasterCmd() {
		//TODO 
		return new PushChangeSeatMasterCmd();
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
