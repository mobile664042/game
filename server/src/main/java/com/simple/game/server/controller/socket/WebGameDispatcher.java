package com.simple.game.server.controller.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.simple.game.core.domain.cmd.req.HeartCmd;
import com.simple.game.core.domain.cmd.req.ReqCmd;
import com.simple.game.core.domain.cmd.req.game.ReqApplyManagerCmd;
import com.simple.game.core.domain.cmd.req.game.ReqChangeManagerCmd;
import com.simple.game.core.domain.cmd.req.game.ReqChatCmd;
import com.simple.game.core.domain.cmd.req.game.ReqChatMultiCmd;
import com.simple.game.core.domain.cmd.req.game.ReqConnectCmd;
import com.simple.game.core.domain.cmd.req.game.ReqDisconnectCmd;
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
import com.simple.game.core.domain.cmd.req.seat.ReqSetSeatSuccessorCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqSitdownCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStandUpCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStopAssistantCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqStopOnlookerCmd;
import com.simple.game.core.domain.cmd.rtn.RtnCmd;
import com.simple.game.core.domain.cmd.rtn.RtnCommonCmd;
import com.simple.game.core.domain.cmd.rtn.game.RtnGameInfoCmd;
import com.simple.game.core.exception.BizException;
import com.simple.game.core.util.GameSession;
import com.simple.game.ddz.domain.cmd.req.seat.ReqPlayCardCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqReadyNextCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqRobLandlordCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqSurrenderCmd;
import com.simple.game.ddz.domain.service.DdzService;
import com.simple.game.server.constant.MyConstant;
import com.simple.game.server.filter.OnlineAccount;
import com.simple.game.server.filter.OnlineAccount.GameOnlineInfo;

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
	public void onMessage(String gameCode, String message, OnlineAccount onlineAccount) {
		if(MyConstant.DDZ.equals(gameCode)) {
			ReqCmd reqCmd = parseAndCheck(message);
			reqCmd.setPlayerId(onlineAccount.getUser().getId());
			if(reqCmd instanceof ReqJoinCmd) {
				doDdzJoin((ReqJoinCmd)reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof HeartCmd) {
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqGetOnlineListCmd) {
				RtnCmd rtnCmd = ddzService.getOnlineList((ReqGetOnlineListCmd)reqCmd);
				writeRtnCmd(rtnCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqLeftCmd) {
				ddzService.left((ReqLeftCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqChatCmd) {
				ddzService.chat((ReqChatCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqChatMultiCmd) {
				ddzService.chat((ReqChatMultiCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqRewardCmd) {
				ddzService.reward((ReqRewardCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqApplyManagerCmd) {
				ddzService.applyManager((ReqApplyManagerCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqChangeManagerCmd) {
				ddzService.changeManager((ReqChangeManagerCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqKickoutCmd) {
				ddzService.kickout((ReqKickoutCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqResumeCmd) {
				ddzService.resume((ReqResumeCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqPauseCmd) {
				ddzService.pause((ReqPauseCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			
			
			else if(reqCmd instanceof ReqSitdownCmd) {
				RtnCmd rtnCmd = ddzService.sitdown((ReqSitdownCmd)reqCmd);
				//把结果写回给用户
				GameOnlineInfo gameOnlineInfo = onlineAccount.getOnlineWebSocket().get(MyConstant.DDZ);
				GameSession gameSession = new MyGameSession(gameOnlineInfo.getSession());
				gameSession.write(rtnCmd);
				return ;
			}
			else if(reqCmd instanceof ReqQuickSitdownCmd) {
				RtnCmd rtnCmd = ddzService.quickSitdown((ReqQuickSitdownCmd)reqCmd);
				//把结果写回给用户
				GameOnlineInfo gameOnlineInfo = onlineAccount.getOnlineWebSocket().get(MyConstant.DDZ);
				GameSession gameSession = new MyGameSession(gameOnlineInfo.getSession());
				gameSession.write(rtnCmd);
				return ;
			}
			else if(reqCmd instanceof ReqStandUpCmd) {
				ddzService.standUp((ReqStandUpCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqForceStandUpCmd) {
				ddzService.forceStandUp((ReqForceStandUpCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			
			else if(reqCmd instanceof ReqGetSeatPlayerListCmd) {
				RtnCmd rtnCmd = ddzService.getSeatPlayerList((ReqGetSeatPlayerListCmd)reqCmd);
				writeRtnCmd(rtnCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqGetAssistantListCmd) {
				RtnCmd rtnCmd = ddzService.getAssistantList((ReqGetAssistantListCmd)reqCmd);
				writeRtnCmd(rtnCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqApplyAssistantCmd) {
				ddzService.applyAssistant((ReqApplyAssistantCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqApproveApplyAssistantCmd) {
				ddzService.approveApplyAssistant((ReqApproveApplyAssistantCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqStopAssistantCmd) {
				ddzService.stopAssistant((ReqStopAssistantCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqBootAssistantCmd) {
				ddzService.bootAssistant((ReqBootAssistantCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqStopOnlookerCmd) {
				ddzService.stopOnlooker((ReqStopOnlookerCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqBootOnlookerCmd) {
				ddzService.bootOnlooker((ReqBootOnlookerCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqApplySeatSuccessorCmd) {
				ddzService.applySeatSuccessor((ReqApplySeatSuccessorCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqSetSeatSuccessorCmd) {
				ddzService.setSeatSuccessor((ReqSetSeatSuccessorCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			
			else if(reqCmd instanceof ReqApplyBroadcastLiveCmd) {
				ddzService.applyBroadcastLive((ReqApplyBroadcastLiveCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqApproveBroadcastLiveCmd) {
				ddzService.approveBroadcastLive((ReqApproveBroadcastLiveCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqCancleBroadcastLiveCmd) {
				ddzService.cancleBroadcastLive((ReqCancleBroadcastLiveCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqBroadcastLiveCmd) {
				//TODO 需要获取直播数据
				ddzService.broadcastLive((ReqBroadcastLiveCmd)reqCmd, null);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			

			//具体的游戏部分
			else if(reqCmd instanceof ReqPlayCardCmd) {
				ddzService.playCard((ReqPlayCardCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqReadyNextCmd) {
				ddzService.readyNext((ReqReadyNextCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqRobLandlordCmd) {
				ddzService.robLandlord((ReqRobLandlordCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			else if(reqCmd instanceof ReqSurrenderCmd) {
				ddzService.surrender((ReqSurrenderCmd)reqCmd);
				responseReq(reqCmd, onlineAccount);
				return ;
			}
			
			
			
			
			
			
    	}		
	}
	
	private static ReqCmd parseReqCmd(String message) {
		JSONObject jsonObject = JSON.parseObject(message);
		
		int code = jsonObject.getIntValue("code");
		switch(code) {
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void responseReq(ReqCmd reqCmd, OnlineAccount onlineAccount) {
		RtnCommonCmd rtnCmd = reqCmd.valueOfRtnCommonCmd();
		writeRtnCmd(rtnCmd, onlineAccount);
	}
	
	private void writeRtnCmd(RtnCmd rtnCmd, OnlineAccount onlineAccount) {
		//把结果写回给用户
		GameOnlineInfo gameOnlineInfo = onlineAccount.getOnlineWebSocket().get(MyConstant.DDZ);
		GameSession gameSession = new MyGameSession(gameOnlineInfo.getSession());
		gameSession.write(rtnCmd);
	}
	
	
	private void doDdzJoin(ReqJoinCmd reqJoinCmd, OnlineAccount onlineAccount) {
		//传递系统参数
		reqJoinCmd.setPlayerId(onlineAccount.getUser().getId());
		reqJoinCmd.setNickname(onlineAccount.getUser().getNickname());
		reqJoinCmd.setSex(onlineAccount.getUser().getSex());
		reqJoinCmd.setTelphone(onlineAccount.getUser().getTelphone());
		reqJoinCmd.setHeadPic(onlineAccount.getUser().getHeadPic());
		//TODO 
		reqJoinCmd.setBcoin(100000);
		GameOnlineInfo gameOnlineInfo = onlineAccount.getOnlineWebSocket().get(MyConstant.DDZ);
		GameSession gameSession = new MyGameSession(gameOnlineInfo.getSession());
		reqJoinCmd.setSession(gameSession);
		
		RtnGameInfoCmd rtnGameInfoCmd = ddzService.join(reqJoinCmd);
		gameOnlineInfo.setPlayKind(reqJoinCmd.getPlayKind());
		gameOnlineInfo.setDeskNo(reqJoinCmd.getDeskNo());
		
		//把结果写回给用户
		gameSession.write(rtnGameInfoCmd);
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

	public void onClose(String gameCode, OnlineAccount onlineAccount) {
		if(MyConstant.DDZ.equals(gameCode)) {
			GameOnlineInfo gameOnlineInfo = onlineAccount.getOnlineWebSocket().get(MyConstant.DDZ);
			ReqDisconnectCmd reqCmd = new ReqDisconnectCmd();
			reqCmd.setPlayKind(gameOnlineInfo.getPlayKind());
			reqCmd.setDeskNo(gameOnlineInfo.getDeskNo());
			reqCmd.setPlayerId(onlineAccount.getUser().getId());
			
			//TODO 
			ddzService.disconnect(reqCmd);
    	}			
	}

	public void onReOpen(String gameCode, OnlineAccount onlineAccount) {
		if(MyConstant.DDZ.equals(gameCode)) {
			GameOnlineInfo gameOnlineInfo = onlineAccount.getOnlineWebSocket().get(MyConstant.DDZ);
			GameSession newSession = new MyGameSession(gameOnlineInfo.getSession());
			
			ReqConnectCmd reqCmd = new ReqConnectCmd();
			reqCmd.setPlayKind(gameOnlineInfo.getPlayKind());
			reqCmd.setDeskNo(gameOnlineInfo.getDeskNo());
			reqCmd.setPlayerId(onlineAccount.getUser().getId());
			reqCmd.setSession(newSession);	
			
			ddzService.connected(reqCmd);
		}				
	}
	
	
	
	
}