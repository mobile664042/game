package com.simple.game.core.domain.service;

import java.util.ArrayList;
import java.util.List;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.game.PushApplyManagerCmd;
import com.simple.game.core.domain.cmd.push.game.PushChangeManagerCmd;
import com.simple.game.core.domain.cmd.push.game.PushChatMultiCmd;
import com.simple.game.core.domain.cmd.push.game.PushKickoutCmd;
import com.simple.game.core.domain.cmd.push.game.PushRewardCmd;
import com.simple.game.core.domain.cmd.push.seat.PushApplyAssistantCmd;
import com.simple.game.core.domain.cmd.push.seat.PushApplyBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.push.seat.PushApproveApplyAssistantCmd;
import com.simple.game.core.domain.cmd.push.seat.PushApproveBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.push.seat.PushBootAssistantCmd;
import com.simple.game.core.domain.cmd.push.seat.PushBootOnlookerCmd;
import com.simple.game.core.domain.cmd.push.seat.PushBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.push.seat.PushCancleBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.push.seat.PushSetSeatSuccessorCmd;
import com.simple.game.core.domain.cmd.push.seat.PushSitdownCmd;
import com.simple.game.core.domain.cmd.push.seat.PushStandUpCmd;
import com.simple.game.core.domain.cmd.push.seat.PushStopAssistantCmd;
import com.simple.game.core.domain.cmd.push.seat.PushStopOnlookerCmd;
import com.simple.game.core.domain.cmd.req.game.ReqApplyManagerCmd;
import com.simple.game.core.domain.cmd.req.game.ReqChangeManagerCmd;
import com.simple.game.core.domain.cmd.req.game.ReqChatMultiCmd;
import com.simple.game.core.domain.cmd.req.game.ReqKickoutCmd;
import com.simple.game.core.domain.cmd.req.game.ReqPauseCmd;
import com.simple.game.core.domain.cmd.req.game.ReqResumeCmd;
import com.simple.game.core.domain.cmd.req.game.ReqRewardCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApplyAssistantCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApplyBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApplySeatSuccessorCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApproveApplyAssistantCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApproveBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqBootAssistantCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqBootOnlookerCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqBroadcastLiveCmd;
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
		
		SeatPlayerVo vo = new SeatPlayerVo();
		vo.setId(player.getId());
		vo.setNickname(player.getNickname());
		vo.setGameLevel(player.getGameLevel());
		vo.setExpValue(player.getExpValue());
		vo.setVipLevel(player.getVipLevel());
		vo.setHeadPic(player.getHeadPic());
		vo.setSeatPost(rtnCmd.getSeatPost());
		vo.setPosition(rtnCmd.getPosition());
		pushCmd.setPlayer(vo);
		
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
		SeatPlayerVo vo = new SeatPlayerVo();
		vo.setId(player.getId());
		vo.setNickname(player.getNickname());
		vo.setGameLevel(player.getGameLevel());
		vo.setExpValue(player.getExpValue());
		vo.setVipLevel(player.getVipLevel());
		vo.setHeadPic(player.getHeadPic());
		vo.setSeatPost(rtnCmd.getSeatPost());
		vo.setPosition(rtnCmd.getPosition());
		pushCmd.setPlayer(vo);
		
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
		pushCmd.setNickname(outParam.getParam().getNickname());
		pushCmd.setHeadPic(outParam.getParam().getHeadPic());
		
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
		pushCmd.setNickname(outParam.getParam().getNickname());
		pushCmd.setHeadPic(outParam.getParam().getHeadPic());
		
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
		pushCmd.setHeadPic(outParam.getParam().getHeadPic());
		
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
	 * 申请(下一轮)主席位继任人
	 * @param player
	 */
	public final void applySeatSuccessor(ReqApplySeatSuccessorCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		tableGame.applySeatSuccessor(reqCmd.getPlayerId(), reqCmd.getPosition(), outParam);
	}
	
	/***
	 * 设置(下一轮)主席位继任人
	 * @param player
	 */
	public final void setSeatSuccessor(ReqSetSeatSuccessorCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		tableGame.setSeatSuccessor(reqCmd.getPlayerId(), reqCmd.getPosition(), reqCmd.getOtherId(), outParam);
		PushSetSeatSuccessorCmd pushCmd = reqCmd.valueOfPushSetSeatSuccessorCmd();
		pushCmd.setNickname(outParam.getParam().getNickname());
		pushCmd.setHeadPic(outParam.getParam().getHeadPic());
		
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
		pushCmd.setHeadPic(outParam.getParam().getHeadPic());
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
		pushCmd.setHeadPic(outParam.getParam().getHeadPic());
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
		pushCmd.setHeadPic(outParam.getParam().getHeadPic());
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	

	/***申请直播****/
	public void applyBroadcastLive(ReqApplyBroadcastLiveCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<SeatPlayer> outParam = OutParam.build();
		boolean result = tableGame.applyBroadcastLive(reqCmd.getPlayerId(), outParam);
		if(result) {
			PushApplyBroadcastLiveCmd pushCmd = reqCmd.valueOfPushApplyBroadcastLiveCmd();
			pushCmd.setNickname(outParam.getParam().getPlayer().getNickname());
			pushCmd.setHeadPic(outParam.getParam().getPlayer().getHeadPic());
			tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
		}
	}
	/***取消直播****/
	public void cancleBroadcastLive(ReqCancleBroadcastLiveCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		
		tableGame.cancleBroadcastLive(reqCmd.getPlayerId());
		PushCancleBroadcastLiveCmd pushCmd = reqCmd.valueOfPushCancleBroadcastLiveCmd();
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	/***直播****/
	public void broadcastLive(ReqBroadcastLiveCmd reqCmd, byte[] data) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		//TODO
		tableGame.broadcastLive(reqCmd.getPlayerId(), data);
		PushBroadcastLiveCmd pushCmd = reqCmd.valueOfPushBroadcastLiveCmd();
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	/***
	 * 管理员同意XXX的直播
	 * @param player
	 * @param id
	 */
	public void approveBroadcastLive(ReqApproveBroadcastLiveCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		tableGame.approveBroadcastLive(reqCmd.getPlayerId(), reqCmd.getPosition());
		PushApproveBroadcastLiveCmd pushCmd = reqCmd.valueOfPushApproveBroadcastLiveCmd();
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	/***
	 * 申请成为管理员
	 * 
	 * 当无管理员时第一个自动成功管理员
	 * 
	 * @param player
	 * @param position
	 */
	public void applyManager(ReqApplyManagerCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		boolean result = tableGame.applyManager(reqCmd.getPlayerId(), outParam);
		if(result) {
			PushApplyManagerCmd pushCmd = reqCmd.valueOfPushApplyManagerCmd();
			pushCmd.setNickname(outParam.getParam().getNickname());
			pushCmd.setHeadPic(outParam.getParam().getHeadPic());
			tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
		}
	}
	
	/***
	 * 更改管理员
	 * @param player
	 * @param playerId 为空时，表示无管理人员
	 */
	public void changeManager(ReqChangeManagerCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		tableGame.changeManager(reqCmd.getPlayerId(), reqCmd.getOtherId(), outParam);
		
		PushChangeManagerCmd pushCmd = reqCmd.valueOfPushChatMultiCmd();
		pushCmd.setNickname(outParam.getParam().getNickname());
		pushCmd.setHeadPic(outParam.getParam().getHeadPic());
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	/***
	 * 踢人
	 * @param player
	 * @param id
	 */
	public void kickout(ReqKickoutCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		tableGame.kickout(reqCmd.getPlayerId(), reqCmd.getOtherId(), outParam);
		
		PushKickoutCmd pushCmd = reqCmd.valueOfPushChatMultiCmd();
		pushCmd.setNickname(outParam.getParam().getNickname());
		pushCmd.setHeadPic(outParam.getParam().getHeadPic());
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	

	/***游戏暂停****/
	public void pause(ReqPauseCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		if(reqCmd.getSeconds() <= 0) {
			throw new BizException("暂停时长不能小于或等于0");
		}
		OutParam<Player> outParam = OutParam.build();
		tableGame.pause(reqCmd.getPlayerId(), reqCmd.getSeconds(), outParam);

		PushCmd pushCmd = reqCmd.valueOfPushPauseCmd();
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	/***游戏取消暂停(恢复正常)****/
	public void resume(ReqResumeCmd reqCmd) {
		TableGame tableGame = (TableGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		tableGame.resume(reqCmd.getPlayerId(), outParam);
		PushCmd pushCmd = reqCmd.valueOfPushResumeCmd();
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
}
