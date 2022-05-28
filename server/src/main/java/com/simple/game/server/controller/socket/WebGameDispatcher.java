package com.simple.game.server.controller.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.simple.game.core.constant.GameConstant;
import com.simple.game.core.domain.cmd.req.HeartCmd;
import com.simple.game.core.domain.cmd.req.ReqCmd;
import com.simple.game.core.domain.cmd.req.game.ReqApplyManagerCmd;
import com.simple.game.core.domain.cmd.req.game.ReqChangeManagerCmd;
import com.simple.game.core.domain.cmd.req.game.ReqChatCmd;
import com.simple.game.core.domain.cmd.req.game.ReqChatMultiCmd;
import com.simple.game.core.domain.cmd.req.game.ReqConnectCmd;
import com.simple.game.core.domain.cmd.req.game.ReqDisconnectCmd;
import com.simple.game.core.domain.cmd.req.game.ReqGameCmd;
import com.simple.game.core.domain.cmd.req.game.ReqGetOnlineListCmd;
import com.simple.game.core.domain.cmd.req.game.ReqJoinCmd;
import com.simple.game.core.domain.cmd.req.game.ReqKickoutCmd;
import com.simple.game.core.domain.cmd.req.game.ReqLeftCmd;
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
import com.simple.game.core.domain.cmd.req.seat.ReqSeatCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqSetSeatSuccessorCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqSitdownCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStandUpCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStopAssistantCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStopOnlookerCmd;
import com.simple.game.core.domain.cmd.rtn.RtnCmd;
import com.simple.game.core.domain.cmd.rtn.RtnCommonCmd;
import com.simple.game.core.domain.dto.GameSeat;
import com.simple.game.core.domain.dto.GameSessionInfo;
import com.simple.game.core.exception.BizException;
import com.simple.game.core.util.GameSession;
import com.simple.game.ddz.domain.cmd.req.seat.ReqPlayCardCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqReadyNextCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqRobLandlordCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqSurrenderCmd;
import com.simple.game.ddz.domain.service.DdzService;
import com.simple.game.server.constant.MyConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WebGameDispatcher {
	
	
	
	@Autowired
	private DdzService ddzService;


	/***
	 * 游戲轉發處理
	 * @param gameCode
	 * @param message
	 * @param onlineAccount
	 */
	public void onMessage(String gameCode, String message, GameSession gameSession) {
		if(MyConstant.DDZ.equals(gameCode)) {
			GameSessionInfo gameSessionInfo = (GameSessionInfo)gameSession.getAttachment().get(MyConstant.GAME_SESSION_INFO);
			ReqCmd reqCmd = parseAndCheck(message);
			
			if(reqCmd instanceof ReqGameCmd) {
				if(gameSessionInfo.getAddress() == null && !(reqCmd instanceof ReqJoinCmd)) {
					throw new BizException("还未进入游戏不可执行这操作，" + message);
				}
				if(reqCmd instanceof ReqSeatCmd) {
					if(!(gameSessionInfo.getAddress() instanceof GameSeat)) {
						throw new BizException("还未坐下不可执行这操作，" + message);
					}
				}
			}
			
			if(reqCmd instanceof ReqJoinCmd) {
				doDdzJoin((ReqJoinCmd)reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof HeartCmd) {
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqGetOnlineListCmd) {
				ddzService.getOnlineList(gameSessionInfo, (ReqGetOnlineListCmd)reqCmd);
				return ;
			}
			else if(reqCmd instanceof ReqLeftCmd) {
				ddzService.left(gameSessionInfo, (ReqLeftCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqChatCmd) {
				ddzService.chat(gameSessionInfo, (ReqChatCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqChatMultiCmd) {
				ddzService.chat(gameSessionInfo, (ReqChatMultiCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqRewardCmd) {
				ddzService.reward(gameSessionInfo, (ReqRewardCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqApplyManagerCmd) {
				ddzService.applyManager(gameSessionInfo, (ReqApplyManagerCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqChangeManagerCmd) {
				ddzService.changeManager(gameSessionInfo, (ReqChangeManagerCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqKickoutCmd) {
				ddzService.kickout(gameSessionInfo, (ReqKickoutCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqResumeCmd) {
				ddzService.resume(gameSessionInfo, (ReqResumeCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqPauseCmd) {
				ddzService.pause(gameSessionInfo, (ReqPauseCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			
			
			else if(reqCmd instanceof ReqSitdownCmd) {
				ddzService.sitdown(gameSessionInfo, (ReqSitdownCmd)reqCmd);
				return ;
			}
			else if(reqCmd instanceof ReqQuickSitdownCmd) {
				ddzService.quickSitdown(gameSessionInfo, (ReqQuickSitdownCmd)reqCmd);
				return ;
			}
			else if(reqCmd instanceof ReqStandUpCmd) {
				ddzService.standUp(gameSessionInfo, (ReqStandUpCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqForceStandUpCmd) {
				ddzService.forceStandUp(gameSessionInfo, (ReqForceStandUpCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			
			else if(reqCmd instanceof ReqGetSeatPlayerListCmd) {
				ddzService.getSeatPlayerList(gameSessionInfo, (ReqGetSeatPlayerListCmd)reqCmd);
				return ;
			}
			else if(reqCmd instanceof ReqGetAssistantListCmd) {
				ddzService.getAssistantList(gameSessionInfo, (ReqGetAssistantListCmd)reqCmd);
				return ;
			}
			else if(reqCmd instanceof ReqApplyAssistantCmd) {
				ddzService.applyAssistant(gameSessionInfo, (ReqApplyAssistantCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqApproveApplyAssistantCmd) {
				ddzService.approveApplyAssistant(gameSessionInfo, (ReqApproveApplyAssistantCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqStopAssistantCmd) {
				ddzService.stopAssistant(gameSessionInfo, (ReqStopAssistantCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqBootAssistantCmd) {
				ddzService.bootAssistant(gameSessionInfo, (ReqBootAssistantCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqStopOnlookerCmd) {
				ddzService.stopOnlooker(gameSessionInfo, (ReqStopOnlookerCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqBootOnlookerCmd) {
				ddzService.bootOnlooker(gameSessionInfo, (ReqBootOnlookerCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqApplySeatSuccessorCmd) {
				ddzService.applySeatSuccessor(gameSessionInfo, (ReqApplySeatSuccessorCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqSetSeatSuccessorCmd) {
				ddzService.setSeatSuccessor(gameSessionInfo, (ReqSetSeatSuccessorCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			
			else if(reqCmd instanceof ReqApplyBroadcastLiveCmd) {
				ddzService.applyBroadcastLive(gameSessionInfo, (ReqApplyBroadcastLiveCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqApproveBroadcastLiveCmd) {
				ddzService.approveBroadcastLive(gameSessionInfo, (ReqApproveBroadcastLiveCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqCancleBroadcastLiveCmd) {
				ddzService.cancleBroadcastLive(gameSessionInfo, (ReqCancleBroadcastLiveCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqBroadcastLiveCmd) {
				//TODO 需要获取直播数据
				ddzService.broadcastLive(gameSessionInfo, (ReqBroadcastLiveCmd)reqCmd, null);
				responseReq(reqCmd, gameSession);
				return ;
			}
			

			//具体的游戏部分
			else if(reqCmd instanceof ReqPlayCardCmd) {
				ddzService.playCard(gameSessionInfo, (ReqPlayCardCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqReadyNextCmd) {
				ddzService.readyNext(gameSessionInfo, (ReqReadyNextCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			else if(reqCmd instanceof ReqRobLandlordCmd) {
				ddzService.robLandlord(gameSessionInfo, (ReqRobLandlordCmd)reqCmd);
				return ;
			}
			else if(reqCmd instanceof ReqSurrenderCmd) {
				ddzService.surrender(gameSessionInfo, (ReqSurrenderCmd)reqCmd);
				responseReq(reqCmd, gameSession);
				return ;
			}
			
			
			
			
			
			
    	}		
	}
	
	private static ReqCmd parseReqCmd(String message) {
		JSONObject jsonObject = JSON.parseObject(message);
		
		int cmd = jsonObject.getIntValue("cmd");
		switch(cmd) {
		case ReqJoinCmd.CMD:
			return JSON.parseObject(message, ReqJoinCmd.class);
		case HeartCmd.CMD:
			return JSON.parseObject(message, HeartCmd.class);
		case ReqGetOnlineListCmd.CMD:
			return JSON.parseObject(message, ReqGetOnlineListCmd.class);
		case ReqLeftCmd.CMD:
			return JSON.parseObject(message, ReqLeftCmd.class);
			
		case ReqChatCmd.CMD:
			return JSON.parseObject(message, ReqChatCmd.class);
		case ReqChatMultiCmd.CMD:
			return JSON.parseObject(message, ReqChatMultiCmd.class);
		case ReqRewardCmd.CMD:
			return JSON.parseObject(message, ReqRewardCmd.class);
			
		case ReqApplyManagerCmd.CMD:
			return JSON.parseObject(message, ReqApplyManagerCmd.class);
		case ReqChangeManagerCmd.CMD:
			return JSON.parseObject(message, ReqChangeManagerCmd.class);
		case ReqKickoutCmd.CMD:
			return JSON.parseObject(message, ReqKickoutCmd.class);
		case ReqResumeCmd.CMD:
			return JSON.parseObject(message, ReqResumeCmd.class);
		case ReqPauseCmd.CMD:
			return JSON.parseObject(message, ReqPauseCmd.class);
			
		
		//桌位请求
		case ReqSitdownCmd.CMD:
			return JSON.parseObject(message, ReqSitdownCmd.class);
		case ReqQuickSitdownCmd.CMD:
			return JSON.parseObject(message, ReqQuickSitdownCmd.class);
		case ReqStandUpCmd.CMD:
			return JSON.parseObject(message, ReqStandUpCmd.class);
		case ReqForceStandUpCmd.CMD:
			return JSON.parseObject(message, ReqForceStandUpCmd.class);
			
		case ReqGetSeatPlayerListCmd.CMD:
			return JSON.parseObject(message, ReqGetSeatPlayerListCmd.class);
		case ReqGetAssistantListCmd.CMD:
			return JSON.parseObject(message, ReqGetAssistantListCmd.class);
		case ReqApplyAssistantCmd.CMD:
			return JSON.parseObject(message, ReqApplyAssistantCmd.class);
		case ReqApproveApplyAssistantCmd.CMD:
			return JSON.parseObject(message, ReqApproveApplyAssistantCmd.class);
		case ReqStopAssistantCmd.CMD:
			return JSON.parseObject(message, ReqStopAssistantCmd.class);
		case ReqBootAssistantCmd.CMD:
			return JSON.parseObject(message, ReqBootAssistantCmd.class);
		case ReqStopOnlookerCmd.CMD:
			return JSON.parseObject(message, ReqStopOnlookerCmd.class);
		case ReqBootOnlookerCmd.CMD:
			return JSON.parseObject(message, ReqBootOnlookerCmd.class);
		case ReqApplySeatSuccessorCmd.CMD:
			return JSON.parseObject(message, ReqApplySeatSuccessorCmd.class);
		case ReqSetSeatSuccessorCmd.CMD:
			return JSON.parseObject(message, ReqSetSeatSuccessorCmd.class);
		
		case ReqApplyBroadcastLiveCmd.CMD:
			return JSON.parseObject(message, ReqApplyBroadcastLiveCmd.class);
		case ReqApproveBroadcastLiveCmd.CMD:
			return JSON.parseObject(message, ReqApproveBroadcastLiveCmd.class);
		case ReqCancleBroadcastLiveCmd.CMD:
			return JSON.parseObject(message, ReqCancleBroadcastLiveCmd.class);
		case ReqBroadcastLiveCmd.CMD:
			return JSON.parseObject(message, ReqBroadcastLiveCmd.class);
			
			
			
		//TODO 
		case ReqPlayCardCmd.CMD:
			return JSON.parseObject(message, ReqPlayCardCmd.class);
		case ReqReadyNextCmd.CMD:
			return JSON.parseObject(message, ReqReadyNextCmd.class);
		case ReqRobLandlordCmd.CMD:
			return JSON.parseObject(message, ReqRobLandlordCmd.class);
		case ReqSurrenderCmd.CMD:
			return JSON.parseObject(message, ReqSurrenderCmd.class);
			
		
		}
		throw new BizException("无效的参数3：" + message);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void responseReq(ReqCmd reqCmd, GameSession gameSession) {
		RtnCommonCmd rtnCmd = reqCmd.valueOfRtnCommonCmd();
		writeRtnCmd(rtnCmd, gameSession);
	}
	
	private void writeRtnCmd(RtnCmd rtnCmd, GameSession gameSession) {
		gameSession.write(rtnCmd);
	}
	
	
	private void doDdzJoin(ReqJoinCmd reqJoinCmd, GameSession gameSession) {
//    	long playerId = (Long)gameSession.getAttachment().get(MyConstant.PLAYER_ID);
//    	String nickName = (String)gameSession.getAttachment().get(MyConstant.NICKNAME);
//    	int sex = (Integer)gameSession.getAttachment().get(MyConstant.SEX);
//    	String telphone = (String)gameSession.getAttachment().get(MyConstant.TELPHONE);
//    	int headPic = (Integer)gameSession.getAttachment().get(MyConstant.HEADPIC);
//    	reqJoinCmd.setPlayerId(playerId);
//    	reqJoinCmd.setNickname(nickName);
//    	reqJoinCmd.setSex(sex);
//    	reqJoinCmd.setTelphone(telphone);
//    	reqJoinCmd.setHeadPic(headPic);
		//TODO 
		reqJoinCmd.setBcoin(100000);

		ddzService.join(reqJoinCmd, gameSession);
	}
	
	private static ReqCmd parseAndCheck(String message) {
		ReqCmd reqCmd = null;
		try {
			reqCmd = parseReqCmd(message);
		}
		catch(Exception e) {
			log.warn("无效的参数1：" + message);
			throw new BizException("无效的参数1：" + message, e);
		}
		
		if(reqCmd == null) {
			log.warn("无效的参数2：" + message);
			throw new BizException("无效的参数2：" + message);
		}
		
		reqCmd.checkParam();
		return reqCmd;
    }

	public void onClose(String gameCode, GameSession gameSession) {
		if(MyConstant.DDZ.equals(gameCode)) {
			ReqDisconnectCmd reqCmd = new ReqDisconnectCmd();
//	    	int playKind = (Integer)gameSession.getAttachment().get(MyConstant.PLAY_KIND);
//	    	int deskNo = (Integer)gameSession.getAttachment().get(MyConstant.DESK_NO);
//	    	long playerId = (Long)gameSession.getAttachment().get(MyConstant.PLAYER_ID);

//	    	reqCmd.setPlayKind(playKind);
//			reqCmd.setDeskNo(deskNo);
//			reqCmd.setPlayerId(playerId);
			GameSessionInfo gameSessionInfo = (GameSessionInfo)gameSession.getAttachment().get(GameConstant.GAME_SESSION_INFO);
			ddzService.disconnect(gameSessionInfo, reqCmd);
    	}			
	}

	public void onReOpen(String gameCode, GameSession gameSession) {
		if(MyConstant.DDZ.equals(gameCode)) {
			ReqConnectCmd reqCmd = new ReqConnectCmd();
//	    	int playKind = (Integer)gameSession.getAttachment().get(MyConstant.PLAY_KIND);
//	    	int deskNo = (Integer)gameSession.getAttachment().get(MyConstant.DESK_NO);
//	    	long playerId = (Long)gameSession.getAttachment().get(MyConstant.PLAYER_ID);

//	    	reqCmd.setPlayKind(playKind);
//			reqCmd.setDeskNo(deskNo);
//			reqCmd.setPlayerId(playerId);
//			reqCmd.setSession(gameSession);
			
			ddzService.connected(gameSession, reqCmd);
		}				
	}
	
	
	
	
}