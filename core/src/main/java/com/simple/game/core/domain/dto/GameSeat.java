package com.simple.game.core.domain.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.constant.GameConstant;
import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.game.notify.PushNotifyApplySeatSuccessorCmd;
import com.simple.game.core.domain.cmd.push.game.notify.PushNotifyChangeSeatMasterCmd;
import com.simple.game.core.domain.cmd.push.seat.PushApplyAssistantCmd;
import com.simple.game.core.domain.cmd.push.seat.PushApproveApplyAssistantCmd;
import com.simple.game.core.domain.cmd.push.seat.PushBootAssistantCmd;
import com.simple.game.core.domain.cmd.push.seat.PushBootOnlookerCmd;
import com.simple.game.core.domain.cmd.push.seat.PushSetSeatSuccessorCmd;
import com.simple.game.core.domain.cmd.push.seat.PushSitdownCmd;
import com.simple.game.core.domain.cmd.push.seat.PushStandUpCmd;
import com.simple.game.core.domain.cmd.push.seat.PushStopAssistantCmd;
import com.simple.game.core.domain.cmd.push.seat.PushStopOnlookerCmd;
import com.simple.game.core.domain.cmd.push.seat.notify.PushNotifyApplyAssistantCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApplyAssistantCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApplySeatSuccessorCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApproveApplyAssistantCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqBootAssistantCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqBootOnlookerCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqForceStandUpCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqGetAssistantListCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqGetSeatPlayerListCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqSetSeatSuccessorCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqSitdownCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStandUpCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStopAssistantCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStopOnlookerCmd;
import com.simple.game.core.domain.cmd.rtn.seat.RtnGameSeatInfoCmd;
import com.simple.game.core.domain.cmd.rtn.seat.RtnGetAssistantListCmd;
import com.simple.game.core.domain.cmd.rtn.seat.RtnGetSeatPlayerListCmd;
import com.simple.game.core.domain.cmd.vo.DdzSeatPlayerVo;
import com.simple.game.core.domain.dto.constant.SeatPost;
import com.simple.game.core.exception.BizException;
import com.simple.game.core.util.SimpleUtil;

import lombok.Getter;

/***
 * 游戏席位
 * 
 * @author zhibozhang
 *
 */
@Getter
public class GameSeat implements AddressNo{
	private static Logger logger = LoggerFactory.getLogger(GameSeat.class);

	/***所属的桌子****/
	protected final TableDesk desk;
	
	/***席位号从1号开始****/
	protected final int position;
	
	/***席位主要人员****/
	protected final AtomicReference<SeatPlayer> master = new AtomicReference<SeatPlayer>();
	
	protected final ConcurrentHashMap<String, SeatPlugin> pluginMap = new ConcurrentHashMap<String, SeatPlugin>();
	
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
	
	void putPlugin(SeatPlugin plugin) {
		pluginMap.put(plugin.getPluginName(), plugin);
	}
	
	public SeatPlugin getPlugin(String pluginName) {
		return pluginMap.get(pluginName);
	}
	
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
		if(nextMaster == null || master.get() == null) {
			return;
		}
		
		try {
			//判断游戏币是否足够
			this.preSitdown(nextMaster.player);
			master.set(nextMaster);
			nextMaster = null;
			
			PushNotifyChangeSeatMasterCmd pushCmd = new PushNotifyChangeSeatMasterCmd();
			pushCmd.setPosition(position);
			pushCmd.setPlayerId(master.get().getPlayer().getId());
			pushCmd.setNickname(master.get().getPlayer().getNickname());
			pushCmd.setHeadPic(master.get().getPlayer().getHeadPic());
			this.getDesk().broadcast(pushCmd, true);
			logger.info("{}的主席位由{}担任", this.position, master.get().player.getId());
		}
		catch(BizException e) {
			SeatPlayer old = nextMaster;
			nextMaster = null;
			
			PushNotifyChangeSeatMasterCmd pushCmd = new PushNotifyChangeSeatMasterCmd();
			pushCmd.setPosition(position);
			pushCmd.setPlayerId(old.getPlayer().getId());
			pushCmd.setNickname(old.getPlayer().getNickname());
			pushCmd.setHeadPic(old.getPlayer().getHeadPic());
			this.getDesk().broadcast(pushCmd, true);
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
		List<Long> list = new ArrayList<Long>(this.seatPlayerMap.keySet());
		logger.warn("准备站起席位所有人：" + list);
		for(long playerId : list) {
			this.standUp(playerId, false);
		}
	}
	
	protected void preSitdown(Player player) {}
	
	public final void sitdown(GameSessionInfo gameSessionInfo, ReqSitdownCmd reqCmd) {
		desk.operatorVerfy();
		
		Player player = desk.getPlayer(gameSessionInfo.getPlayerId());
		preSitdown(player);
		//判断是否经坐下
		SeatPlayer old = seatPlayerMap.get(player.getId());
		if(old != null) {
			throw new BizException(String.format("已经在桌位%s中，不可再坐下", reqCmd.getPosition()));
		}
		
		SeatPlayer seatPlayer = null;
		//直接成为主席位
		if(this.master.get() == null) {
			seatPlayer = buildSeatPlayer(player, SeatPost.master);
			if(this.master.compareAndSet(null, seatPlayer)) {
				doSitdownMaster();
			}
			else {
				throw new BizException(String.format("主席之位已被%s抢走了，请重试吧", player.getNickname()));
			}
		}
		else {
			//判断是否超过最大限度，不需要加锁判断，减少时死锁，提高性能，允许极少量的误差
			if(seatPlayerMap.size() >= desk.getGameItem().getSeatMaxFans()) {
				throw new BizException(String.format("人员已挤不下去了(已有%s)", desk.getGameItem().getSeatMaxFans()));
			}
			if(this.isStopOnlooker()) {
				throw new BizException(String.format("已禁止旁观！！"));
			}
			seatPlayer = buildSeatPlayer(player, SeatPost.onlooker);
		}
		seatPlayerMap.put(player.getId(), seatPlayer);
		gameSessionInfo.setAddress(this);
		afterSitdownMaster(seatPlayer);
		
		PushSitdownCmd pushCmd = reqCmd.valueOfPushSitdownCmd();
		DdzSeatPlayerVo vo = new DdzSeatPlayerVo();
		vo.setId(player.getId());
		vo.setNickname(player.getNickname());
		vo.setGameLevel(player.getGameLevel());
		vo.setExpValue(player.getExpValue());
		vo.setVipLevel(player.getVipLevel());
		vo.setHeadPic(player.getHeadPic());
		vo.setSeatPost(seatPlayer.getSeatPost());
		vo.setPosition(reqCmd.getPosition());
		pushCmd.setPlayer(vo);
		//发送广播
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
		
		RtnGameSeatInfoCmd rtnCmd =  this.getGameSeatInfo();
		rtnCmd.setSeatPost(seatPlayer.getSeatPost());
		player.getOnline().getSession().write(rtnCmd);		
	}
	
	protected void doSitdownMaster() {
	}
	protected void afterSitdownMaster(SeatPlayer seatPlayer) {
	}
	
	public void getSeatPlayerList(GameSessionInfo gameSessionInfo, ReqGetSeatPlayerListCmd reqCmd) {
		Player player = desk.getPlayer(gameSessionInfo.getPlayerId());
		List<SeatPlayer> list = new ArrayList<SeatPlayer>(seatPlayerMap.values());
		int fromIndex = reqCmd.getFromPage() * TableDesk.PAGE_SIZE;
		int toIndex = fromIndex + TableDesk.PAGE_SIZE;
		
		List<DdzSeatPlayerVo> voList = new ArrayList<DdzSeatPlayerVo>(list.size());
		RtnGetSeatPlayerListCmd rtnCmd = new RtnGetSeatPlayerListCmd();
		rtnCmd.setList(voList);
		for(int i=fromIndex; i<list.size() && i<toIndex; i++) {
			SeatPlayer seatPlayer = list.get(i);
			DdzSeatPlayerVo vo = seatPlayer.valueOfSeatPlayerVo();
			voList.add(vo);
		}
		player.getOnline().getSession().write(rtnCmd);
	}
	public void getAssistantList(GameSessionInfo gameSessionInfo, ReqGetAssistantListCmd reqCmd) {
		Player player = desk.getPlayer(gameSessionInfo.getPlayerId());
		List<SeatPlayer> list = new ArrayList<SeatPlayer>(assistantMap.values());
		
		List<DdzSeatPlayerVo> voList = new ArrayList<DdzSeatPlayerVo>(list.size());
		RtnGetAssistantListCmd rtnCmd = new RtnGetAssistantListCmd();
		rtnCmd.setList(voList);
		for(int i=0; i<list.size(); i++) {
			SeatPlayer seatPlayer = list.get(i);
			DdzSeatPlayerVo vo = seatPlayer.valueOfSeatPlayerVo();
			voList.add(vo);
		}
		player.getOnline().getSession().write(rtnCmd);
	}
	
	/***
	 * 获取座位的在线信息
	 * 此处不需要验证玩家的position是否与请求的position是否为同一个
	 * @param playerId
	 * @param position
	 * @return
	 */
	protected SeatPlayer buildSeatPlayer(Player player, SeatPost seatPost){
		return new SeatPlayer(player, this, seatPost);
	}
	
	public void applyAssistant(GameSessionInfo gameSessionInfo, ReqApplyAssistantCmd reqCmd) {
		desk.operatorVerfy();
		long playerId = gameSessionInfo.getPlayerId();
		if(master.get() == null) {
			throw new BizException(String.format("不存在主席位了, 不可以申请辅助"));
		}
		if(master.get().getPlayer().getId() == playerId) {
			throw new BizException(String.format("已经是主席位了不可以申请辅助"));
		}
		
		SeatPlayer target = this.seatPlayerMap.get(playerId);
		if(target.getSeatPost() == SeatPost.assistant) {
			throw new BizException(String.format("已经是辅助了不可以申请辅助"));
		}
		//判断是否不允许助手
		if(target.getGameSeat().isStopAssistant()) {
			throw new BizException(String.format("主席位设置不允许申请助手"));
		}
		
		target.applyAssistanted = true;
		
		Player player = target.getPlayer();
		//发送到主席位中去
		{
			PushNotifyApplyAssistantCmd pushCmd = new PushNotifyApplyAssistantCmd();
			pushCmd.setPlayerId(position);
			pushCmd.setNickname(player.getNickname());
			pushCmd.setHeadPic(player.getHeadPic());
			master.get().getPlayer().getOnline().push(pushCmd);
			logger.info("{}向主席位{}发送辅助申请", target.getPlayer().getNickname(), master.get().getPlayer().getNickname());
		}
		
		PushApplyAssistantCmd pushCmd = reqCmd.valueOfPushApplyAssistantCmd();
		pushCmd.setPosition(position);
		pushCmd.setPlayerId(player.getId());
		pushCmd.setNickname(player.getNickname());
		pushCmd.setHeadPic(player.getHeadPic());
		
		//发送广播
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}
	
	
	public void approveApplyAssistant(GameSessionInfo gameSessionInfo, ReqApproveApplyAssistantCmd reqCmd) {
		desk.operatorVerfy();
		checkSeatMaster(gameSessionInfo.getPlayerId());

		SeatPlayer other = this.seatPlayerMap.get(reqCmd.getOtherId());
		if(other == null) {
			throw new BizException(String.format("%s不在席位上，不可以同意申请辅助", reqCmd.getOtherId()));
		}
		if(other.getGameSeat().getPosition() != position) {
			throw new BizException(String.format("%s与%s不在同一个席位，不可以同意申请辅助", gameSessionInfo.getPlayerId(), reqCmd.getOtherId()));
		}
		if(!other.isApplyAssistanted()) {
			throw new BizException(String.format("%s并没有申请辅助", reqCmd.getOtherId()));
		}
		
		other.getGameSeat().assistantMap.put(other.getPlayer().getId(), other);
		other.seatPost = SeatPost.assistant;
		other.applyAssistanted = false;
		
		PushApproveApplyAssistantCmd pushCmd = reqCmd.valueOfPushApplyAssistantCmd();
		pushCmd.setPosition(position);
		pushCmd.setPlayerId(other.getPlayer().getId());
		pushCmd.setNickname(other.getPlayer().getNickname());
		pushCmd.setHeadPic(other.getPlayer().getHeadPic());
		
		//发送广播
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}


	/***操作验证***/
	protected void checkSeatMaster(long playerId) {
		if(master.get() == null || master.get().getPlayer() == null || master.get().getPlayer().getId() != playerId) {
			throw new BizException(String.format("你不在主席位上"));
		}
	}

	public void stopOnlooker(GameSessionInfo gameSessionInfo, ReqStopOnlookerCmd reqCmd) {
		checkSeatMaster(gameSessionInfo.getPlayerId());
		stopOnlooker = false;
		PushStopOnlookerCmd pushCmd = reqCmd.valueOfPushStopOnlookerCmd();
		pushCmd.setPosition(position);
		//发送广播
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}
	public void stopAssistant(GameSessionInfo gameSessionInfo, ReqStopAssistantCmd reqCmd) {
		checkSeatMaster(gameSessionInfo.getPlayerId());
		stopAssistant = true;
		PushStopAssistantCmd pushCmd = reqCmd.valueOfPushStopAssistantCmd();
		pushCmd.setPosition(position);
		
		//发送广播
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}

	public void bootOnlooker(GameSessionInfo gameSessionInfo, ReqBootOnlookerCmd reqCmd) {
		checkSeatMaster(gameSessionInfo.getPlayerId());
		stopOnlooker = false;
		PushBootOnlookerCmd pushCmd = reqCmd.valueOfPushBootOnlookerCmd();
		pushCmd.setPosition(position);
		//发送广播
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}
	public void bootAssistant(GameSessionInfo gameSessionInfo, ReqBootAssistantCmd reqCmd) {
		checkSeatMaster(gameSessionInfo.getPlayerId());
		stopAssistant = false;
		PushBootAssistantCmd pushCmd = reqCmd.valueOfPushBootAssistantCmd();
		pushCmd.setPosition(position);
		//发送广播
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}
	protected void preStandUp(SeatPlayer seatPlayer) {};

	public void applySeatSuccessor(GameSessionInfo gameSessionInfo, ReqApplySeatSuccessorCmd reqCmd) {
		if(gameSessionInfo.getPlayerId() == this.master.get().getPlayer().getId()) {
			throw new BizException(String.format("你已是主席位"));
		}
		
		SeatPlayer other = this.seatPlayerMap.get(gameSessionInfo.getPlayerId());
		if(other == null) {
			throw new BizException(String.format("%s不在席位上，不可以申请席位继任者", gameSessionInfo.getPlayerId()));
		}
		Player player = other.getPlayer();
		
		//向主席位发送告知
		PushNotifyApplySeatSuccessorCmd pushCmd = new PushNotifyApplySeatSuccessorCmd();
		pushCmd.setPlayerId(player.getId());
		pushCmd.setHeadPic(player.getHeadPic());
		pushCmd.setNickname(player.getNickname());
		master.get().getPlayer().getOnline().push(pushCmd);
		logger.info("{}向主席位{}发送更换管理员申请", player.getNickname(), master.get().getPlayer().getNickname());
	}
	
	public void setSeatSuccessor(GameSessionInfo gameSessionInfo, ReqSetSeatSuccessorCmd reqCmd) {
		desk.operatorVerfy();
		checkSeatMaster(gameSessionInfo.getPlayerId());

		SeatPlayer other = this.seatPlayerMap.get(reqCmd.getOtherId());
		if(other == null) {
			throw new BizException(String.format("%s不在席位上，不可以设置席位继认者", gameSessionInfo.getPlayerId()));
		}
		if(other.getGameSeat().getPosition() != position) {
			throw new BizException(String.format("%s与%s不在同一个席位，不可以设置席位继认者", master.get().getPlayer().getId(), reqCmd.getOtherId()));
		}
		if(!other.isApplyAssistanted()) {
			throw new BizException(String.format("%s不是辅助，不可以成为席位继认者", reqCmd.getOtherId()));
		}
		
		this.nextMaster = other;
		Player player = other.getPlayer();
		
		PushSetSeatSuccessorCmd pushCmd = reqCmd.valueOfPushSetSeatSuccessorCmd();
		pushCmd.setPlayerId(player.getId());
		pushCmd.setNickname(player.getNickname());
		pushCmd.setHeadPic(player.getHeadPic());
		pushCmd.setPosition(position);
		
		//发送广播
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}
	
	/***
	 * 从席位中站起来
	 * @param player
	 */
	public final void standUp(GameSessionInfo gameSessionInfo, ReqStandUpCmd reqCmd) {
		standUp(gameSessionInfo.getPlayerId());
	}
	
	public void standUp(long playerId) {
		standUp(playerId, true);
	}
	/***
	 * 从席位中站起来
	 * @param playerId
	 * @param excludeSelf 广播信息的时候否排除自身
	 */
	public void standUp(long playerId, boolean excludeSelf) {
		SeatPlayer seatPlayer = this.seatPlayerMap.get(playerId);
		if(seatPlayer == null) {
			throw new BizException(String.format("%s不在席位上，不可以站起", playerId));
		}
		
		preStandUp(seatPlayer);
		
		Player player = seatPlayer.getPlayer();
		if(seatPlayer.getSeatPost() == SeatPost.master) {
			this.clear();
			master.set(null);
		}
		else if(seatPlayer.getSeatPost() == SeatPost.assistant) {
			//主席位站起
			assistantMap.remove(player.getId());
		}
		seatPlayerMap.remove(player.getId());
		if(player.getOnline().getSession() != null) {
			GameSessionInfo gameSessionInfo = (GameSessionInfo)player.getOnline().getSession().getAttachment().get(GameConstant.GAME_SESSION_INFO);
			gameSessionInfo.setAddress(desk);
		}
		
		PushStandUpCmd pushCmd = new PushStandUpCmd();
		pushCmd.setSeatPost(seatPlayer.getSeatPost());
		pushCmd.setPosition(position);
		pushCmd.setPlayerId(playerId);
		pushCmd.setNickname(player.getNickname());
		pushCmd.setHeadPic(player.getHeadPic());
		
		//发送广播
		if(excludeSelf) {
			desk.broadcast(pushCmd, playerId);
		}
		else {
			desk.broadcast(pushCmd);
		}
	}
	
	protected void clear() {
		this.master.set(null);
		this.nextMaster = null;
		
		this.stopOnlooker = false;
		this.stopAssistant = false;
		this.broadcasting = false;
		this.applyBroadcasted = false;
	}

	public void forceStandUp(GameSessionInfo gameSessionInfo, ReqForceStandUpCmd reqCmd) {
		checkSeatMaster(gameSessionInfo.getPlayerId());

		SeatPlayer other = this.seatPlayerMap.get(reqCmd.getOtherId());
		if(other == null) {
			throw new BizException(String.format("%s不在席位上，不可以强制粉丝站起", reqCmd.getOtherId()));
		}
		if(other.getGameSeat().getPosition() != position) {
			throw new BizException(String.format("%s与%s不在同一个席位，不可以强制粉丝站起", gameSessionInfo.getPlayerId(), reqCmd.getOtherId()));
		}
		if(gameSessionInfo.getPlayerId() == reqCmd.getOtherId()) {
			throw new BizException(String.format("不可以对自己进行强制站起"));
		}
		
		this.standUp(reqCmd.getOtherId());
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

	public void setBroadcasting(boolean broadcasting) {
		this.broadcasting = broadcasting;
	}

	public void setApplyBroadcasted(boolean applyBroadcasted) {
		this.applyBroadcasted = applyBroadcasted;
	}

}
