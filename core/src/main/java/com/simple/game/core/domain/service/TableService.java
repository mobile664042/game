package com.simple.game.core.domain.service;

import java.util.ArrayList;
import java.util.List;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.push.game.PushChatMultiCmd;
import com.simple.game.core.domain.cmd.push.game.PushRewardCmd;
import com.simple.game.core.domain.cmd.push.seat.PushApplyAssistantCmd;
import com.simple.game.core.domain.cmd.push.seat.PushApplyBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.push.seat.PushApproveApplyAssistantCmd;
import com.simple.game.core.domain.cmd.push.seat.PushBootAssistantCmd;
import com.simple.game.core.domain.cmd.push.seat.PushBootOnlookerCmd;
import com.simple.game.core.domain.cmd.push.seat.PushSetSeatSuccessorCmd;
import com.simple.game.core.domain.cmd.push.seat.PushSitdownCmd;
import com.simple.game.core.domain.cmd.push.seat.PushStandUpCmd;
import com.simple.game.core.domain.cmd.push.seat.PushStopAssistantCmd;
import com.simple.game.core.domain.cmd.push.seat.PushStopOnlookerCmd;
import com.simple.game.core.domain.cmd.req.game.ReqChatMultiCmd;
import com.simple.game.core.domain.cmd.req.game.ReqRewardCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApplyAssistantCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApplyBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApproveApplyAssistantCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqBootAssistantCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqBootOnlookerCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqCancleBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqForceStandUpCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqGetAssistantListCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqGetSeatPlayerListCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqQuickSitdownCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqSetSeatSuccessorCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqSitdownCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStandUpCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStopAssistantCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStopOnlookerCmd;
import com.simple.game.core.domain.cmd.rtn.seat.RtnGameSeatInfoCmd;
import com.simple.game.core.domain.cmd.rtn.seat.RtnGetAssistantListCmd;
import com.simple.game.core.domain.cmd.rtn.seat.RtnGetSeatPlayerListCmd;
import com.simple.game.core.domain.cmd.vo.SeatPlayerVo;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.SeatPlayer;
import com.simple.game.core.domain.good.TableGame;
import com.simple.game.core.domain.manager.GameManager;
import com.simple.game.core.exception.BizException;

import lombok.Getter;
import lombok.ToString;

/***
 * 游戏基础包装组件
 * 
 * 游戏中最基础的组件部分
 * 
 * @author zhibozhang
 *
 */
@Getter
@ToString
public abstract class TableService extends BaseService{
//	private final static Logger logger = LoggerFactory.getLogger(TableService.class);
	
	public TableService(GameManager gameManager) {
		super(gameManager);
	}

	/***
	 * 选择某个席位座下
	 * 
	 * @param player
	 * @param position
	 */
	public final RtnGameSeatInfoCmd sitdown(ReqSitdownCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		
		OutParam<Player> outParam = OutParam.build();
		RtnGameSeatInfoCmd rtnCmd = tableGame.sitdown(reqCmd.getPlayerId(), reqCmd.getPosition(), outParam);
		
		
		PushSitdownCmd pushCmd = reqCmd.valueOfPushSitdownCmd();
		Player player = outParam.getParam();
		pushCmd.setPlayer(player.valueOfPlayerVo());
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
		return rtnCmd;
	}
	
	public RtnGetSeatPlayerListCmd getSeatPlayerList(ReqGetSeatPlayerListCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		
		List<SeatPlayer> list = tableGame.getSeatPlayerList(reqCmd.getPosition(), reqCmd.getFromPage());
		
		List<SeatPlayerVo> voList = new ArrayList<SeatPlayerVo>(list.size());
		RtnGetSeatPlayerListCmd rtnCmd = new RtnGetSeatPlayerListCmd();
		rtnCmd.setList(voList);
		for(SeatPlayer player : list) {
			SeatPlayerVo vo = player.valueOfSeatPlayerVo();
			voList.add(vo);
		}
		return rtnCmd;
	}
	
	public RtnGetAssistantListCmd getAssistantList(ReqGetAssistantListCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		
		List<SeatPlayer> list = tableGame.getAssistantList(reqCmd.getPosition());
		
		List<SeatPlayerVo> voList = new ArrayList<SeatPlayerVo>(list.size());
		RtnGetAssistantListCmd rtnCmd = new RtnGetAssistantListCmd();
		rtnCmd.setList(voList);
		for(SeatPlayer player : list) {
			SeatPlayerVo vo = player.valueOfSeatPlayerVo();
			voList.add(vo);
		}
		return rtnCmd;
	}

	/***
	 * 快速坐下
	 * 
	 * @param player
	 */
	public final RtnGameSeatInfoCmd quickSitdown(ReqQuickSitdownCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		
		OutParam<Player> outParam = OutParam.build();
		RtnGameSeatInfoCmd rtnCmd = tableGame.quickSitdown(reqCmd.getPlayerId(), outParam);
		
		
		PushSitdownCmd pushCmd = reqCmd.valueOfPushSitdownCmd();
		pushCmd.setPosition(rtnCmd.getPosition());
		Player player = outParam.getParam();
		pushCmd.setPlayer(player.valueOfPlayerVo());
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
		return rtnCmd;
	}
	
	/***
	 * 申请辅助
	 * @param playerId
	 */
	public void applyAssistant(ReqApplyAssistantCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		tableGame.applyAssistant(reqCmd.getPlayerId(), reqCmd.getPosition(), outParam);
		PushApplyAssistantCmd pushCmd = reqCmd.valueOfPushApplyAssistantCmd();
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	/***
	 * 同意XXX成为辅助
	 * @param player
	 * @param id
	 */
	public void approveApplyAssistant(ReqApproveApplyAssistantCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		tableGame.approveApplyAssistant(reqCmd.getPlayerId(), reqCmd.getPosition(), reqCmd.getOtherId(), outParam);
		PushApproveApplyAssistantCmd pushCmd = reqCmd.valueOfPushApplyAssistantCmd();
		pushCmd.setNickname(outParam.getParam().getNickname());
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	/***
	 * 从席位中站起来
	 * @param player
	 */
	public final void standUp(ReqStandUpCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		tableGame.standUp(reqCmd.getPlayerId(), reqCmd.getPosition(), outParam);
		PushStandUpCmd pushCmd = reqCmd.valueOfPushStandUpCmd();
		pushCmd.setNickname(outParam.getParam().getNickname());
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	
	public void stopAssistant(ReqStopAssistantCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		
		tableGame.stopAssistant(reqCmd.getPlayerId(), reqCmd.getPosition());
		PushStopAssistantCmd pushCmd = reqCmd.valueOfPushStopAssistantCmd();
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	public void stopOnlooker(ReqStopOnlookerCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		
		tableGame.stopOnlooker(reqCmd.getPlayerId(), reqCmd.getPosition());
		PushStopOnlookerCmd pushCmd = reqCmd.valueOfPushStopOnlookerCmd();
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	public void bootAssistant(ReqBootAssistantCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		
		tableGame.bootAssistant(reqCmd.getPlayerId(), reqCmd.getPosition());
		PushBootAssistantCmd pushCmd = reqCmd.valueOfPushBootAssistantCmd();
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());

	}
	public void bootOnlooker(ReqBootOnlookerCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		
		tableGame.bootOnlooker(reqCmd.getPlayerId(), reqCmd.getPosition());
		PushBootOnlookerCmd pushCmd = reqCmd.valueOfPushBootOnlookerCmd();
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	/***
	 * 设置(下一轮)主席位继任人
	 * @param player
	 */
	public final void setSeatSuccessor(ReqSetSeatSuccessorCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		
		tableGame.setSeatSuccessor(reqCmd.getPlayerId(), reqCmd.getPosition(), reqCmd.getOtherId());
		PushSetSeatSuccessorCmd pushCmd = reqCmd.valueOfPushSetSeatSuccessorCmd();
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	/***
	 * 强制席中的某人站起
	 * @param masterId
	 * @param playerId
	 */
	public void forceStandUp(ReqForceStandUpCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		tableGame.forceStandUp(reqCmd.getPlayerId(), reqCmd.getPosition(), reqCmd.getOtherId(), outParam);
		PushStandUpCmd pushCmd = reqCmd.valueOfPushStandUpCmd();
		pushCmd.setNickname(outParam.getParam().getNickname());
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	/***
	 * 在席位中对某个席位进行打赏
	 * @param player
	 */
	public void reward(ReqRewardCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		tableGame.reward(reqCmd.getPlayerId(), reqCmd.getPositionList(), reqCmd.getGift(), outParam);
		PushRewardCmd pushCmd = reqCmd.valueOfPushRewardCmd();
		pushCmd.setNickname(outParam.getParam().getNickname());
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	
	/***聊天****/
	public void chat(ReqChatMultiCmd reqCmd) {
		if(reqCmd.getPositionList() == null || reqCmd.getPositionList().size() == 0) {
			throw new BizException("席位不能为空");
		}
		
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		tableGame.chat(reqCmd.getPlayerId(), reqCmd.getPositionList(), reqCmd.getChat(), outParam);
		PushChatMultiCmd pushCmd = reqCmd.valueOfPushChatMultiCmd();
		pushCmd.setNickname(outParam.getParam().getNickname());
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	

	/***申请直播****/
	public void applyBroadcastLive(ReqApplyBroadcastLiveCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		boolean result = tableGame.applyBroadcastLive(reqCmd.getPlayerId());
		if(result) {
			PushApplyBroadcastLiveCmd pushCmd = reqCmd.valueOfPushApplyBroadcastLiveCmd();
			tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
		}
	}
	/***取消直播****/
	public void cancleBroadcastLive(ReqCancleBroadcastLiveCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		
		tableGame.cancleBroadcastLive(reqCmd.getPlayerId());
		PushSetSeatSuccessorCmd pushCmd = reqCmd.valueOfPushSetSeatSuccessorCmd();
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
		
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
		OutParam<Player> outParam = OutParam.build();
		PushApproveBroadcastLiveCmd pushCmd = getGameDesk().approveBroadcastLive(managerId, position, outParam);
		this.broadcast(pushCmd, managerId);
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
	
}
