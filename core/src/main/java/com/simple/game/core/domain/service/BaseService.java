//package com.simple.game.core.domain.service;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.simple.game.core.constant.GameConstant;
//import com.simple.game.core.domain.cmd.req.game.ReqApplyManagerCmd;
//import com.simple.game.core.domain.cmd.req.game.ReqChangeManagerCmd;
//import com.simple.game.core.domain.cmd.req.game.ReqChatCmd;
//import com.simple.game.core.domain.cmd.req.game.ReqChatMultiCmd;
//import com.simple.game.core.domain.cmd.req.game.ReqConnectCmd;
//import com.simple.game.core.domain.cmd.req.game.ReqDisconnectCmd;
//import com.simple.game.core.domain.cmd.req.game.ReqGetOnlineListCmd;
//import com.simple.game.core.domain.cmd.req.game.ReqJoinCmd;
//import com.simple.game.core.domain.cmd.req.game.ReqKickoutCmd;
//import com.simple.game.core.domain.cmd.req.game.ReqLeftCmd;
//import com.simple.game.core.domain.cmd.req.game.ReqPauseCmd;
//import com.simple.game.core.domain.cmd.req.game.ReqResumeCmd;
//import com.simple.game.core.domain.cmd.req.game.ReqRewardCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqApplyAssistantCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqApplyBroadcastLiveCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqApplySeatSuccessorCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqApproveApplyAssistantCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqApproveBroadcastLiveCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqBootAssistantCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqBootOnlookerCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqBroadcastLiveCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqCancleBroadcastLiveCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqForceStandUpCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqGetAssistantListCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqGetSeatPlayerListCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqQuickSitdownCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqSetSeatSuccessorCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqSitdownCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqStandUpCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqStopAssistantCmd;
//import com.simple.game.core.domain.cmd.req.seat.ReqStopOnlookerCmd;
//import com.simple.game.core.domain.dto.AddressNo;
//import com.simple.game.core.domain.dto.GameSeat;
//import com.simple.game.core.domain.dto.GameSessionInfo;
//import com.simple.game.core.domain.dto.TableDesk;
//import com.simple.game.core.domain.dto.desk.ChatPlugin;
//import com.simple.game.core.domain.dto.desk.RewardPlugin;
//import com.simple.game.core.domain.dto.seat.BroadcastLivePlugin;
//import com.simple.game.core.domain.manager.GameManager;
//import com.simple.game.core.exception.BizException;
//import com.simple.game.core.util.GameSession;
//
//import lombok.Getter;
//
///***
// * 游戏基础包装组件
// * 
// * 游戏中最基础的组件部分
// * 
// * @author zhibozhang
// *
// */
//@Getter
//public abstract class BaseService{
//	private final static Logger logger = LoggerFactory.getLogger(BaseService.class);
//	/***
//	 * 游戏管理
//	 */
//	protected final GameManager gameManager;
//	
//	public BaseService(GameManager gameManager) {
//		this.gameManager = gameManager;
//	}
//	
//	public TableDesk checkAndGet(int playKind, int deskNo) {
//		TableDesk tableDesk = gameManager.getTableDesk(playKind, deskNo);
//		if(tableDesk == null) {
//			throw new BizException("游戏桌没找到!");
//		}
//		return tableDesk;
//	}
//	public TableDesk getTableDesk(AddressNo address) {
//		if(address instanceof TableDesk) {
//			return ((TableDesk)address);
//		}
//		else if(address instanceof GameSeat) {
//			return ((GameSeat)address).getDesk();
//		}
//		logger.warn("address={}识别不了", address);
//		throw new BizException("address识别不了!" + address.getAddrNo());
//	}
//	
//	public TableDesk checkAndGetTableDesk(AddressNo address) {
//		if(address instanceof TableDesk) {
//			return ((TableDesk)address);
//		}
//		throw new BizException("不在游戏桌");
//	}
//	
//	public GameSeat checkAndGetGameSeat(AddressNo address) {
//		if(address instanceof GameSeat) {
//			return ((GameSeat)address);
//		}
//		throw new BizException("还未坐下");
//	}
//
//
//	/***
//	 * 进入游戏
//	 */
//	public void join(ReqJoinCmd reqCmd, GameSession gameSession) {
//    	TableDesk tableDesk = checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
//		tableDesk.join(reqCmd, gameSession);
//	}
//	
//	
//	
//	/***
//	 * 获取游戏在线玩家
//	 */
//	public void getOnlineList(GameSessionInfo gameSessionInfo, ReqGetOnlineListCmd reqCmd) {
//		if(reqCmd.getFromPage() < 0) {
//			throw new BizException("页码是从0开始");
//		}
//		
//		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
//		tableDesk.getOnlineList(gameSessionInfo, reqCmd);
//	}
//	
//	/***
//	 * 离开游戏
//	 */
//	public void left(GameSessionInfo gameSessionInfo, ReqLeftCmd reqCmd) {
//		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
//		tableDesk.left(gameSessionInfo, reqCmd);
//	}
//	
//	
//	/***
//	 * 离开游戏
//	 */
//	public void kickout(GameSessionInfo gameSessionInfo, ReqKickoutCmd reqCmd) {
//		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
//		tableDesk.kickout(gameSessionInfo, reqCmd);
//	}
//	
//	/***聊天****/
//	public void chat(GameSessionInfo gameSessionInfo, ReqChatCmd reqCmd) {
//		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
//		ChatPlugin chatPlugin = (ChatPlugin)tableDesk.getPlugin(ChatPlugin.PLUGIN_NAME);
//		chatPlugin.chat(gameSessionInfo, reqCmd);
//	}
//	
//	/***断网，掉线***/
//	public void disconnect(GameSessionInfo gameSessionInfo, ReqDisconnectCmd reqCmd) {
//		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
//		tableDesk.disconnect(gameSessionInfo, reqCmd);
//	}
//	
//	/***掉线重连***/
//	public void connected(GameSession gameSession, ReqConnectCmd reqCmd) {
//		GameSessionInfo gameSessionInfo = (GameSessionInfo)gameSession.getAttachment().get(GameConstant.GAME_SESSION_INFO);
//		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
//		tableDesk.connect(gameSession, reqCmd);
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	/***
//	 * 选择某个席位座下
//	 * 
//	 * @param player
//	 * @param position
//	 */
//	public final void sitdown(GameSessionInfo gameSessionInfo, ReqSitdownCmd reqCmd) {
//		TableDesk tableDesk = checkAndGetTableDesk(gameSessionInfo.getAddress());
//		GameSeat gameSeat = tableDesk.getGameSeat(reqCmd.getPosition());
//		if(gameSeat == null) {
//			throw new BizException(String.format("%s无效的席位", reqCmd.getPosition()));
//		}
//		gameSeat.sitdown(gameSessionInfo, reqCmd);
//	}
//	
//	public void getSeatPlayerList(GameSessionInfo gameSessionInfo, ReqGetSeatPlayerListCmd reqCmd) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		gameSeat.getSeatPlayerList(gameSessionInfo, reqCmd);
//	}
//	
//	public void getAssistantList(GameSessionInfo gameSessionInfo, ReqGetAssistantListCmd reqCmd) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		gameSeat.getAssistantList(gameSessionInfo, reqCmd);
//	}
//
//	/***
//	 * 快速坐下
//	 * 
//	 * @param player
//	 */
//	public final void quickSitdown(GameSessionInfo gameSessionInfo, ReqQuickSitdownCmd reqCmd) {
//		TableDesk tableDesk = checkAndGetTableDesk(gameSessionInfo.getAddress());
//		GameSeat gameSeat = tableDesk.getIdelGameSeat();
//		if(gameSeat == null) {
//			throw new BizException(String.format("已经没有空闲的席位了"));
//		}
//		gameSeat.sitdown(gameSessionInfo, reqCmd.valueOfReqSitdownCmd(gameSeat.getPosition()));
//	}
//	
//	/***
//	 * 申请辅助
//	 * @param playerId
//	 */
//	public void applyAssistant(GameSessionInfo gameSessionInfo, ReqApplyAssistantCmd reqCmd) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		gameSeat.applyAssistant(gameSessionInfo, reqCmd);
//	}
//	
//	/***
//	 * 同意XXX成为辅助
//	 * @param player
//	 * @param id
//	 */
//	public void approveApplyAssistant(GameSessionInfo gameSessionInfo, ReqApproveApplyAssistantCmd reqCmd) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		gameSeat.approveApplyAssistant(gameSessionInfo, reqCmd);
//	}
//	
//	/***
//	 * 从席位中站起来
//	 * @param player
//	 */
//	public final void standUp(GameSessionInfo gameSessionInfo, ReqStandUpCmd reqCmd) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		gameSeat.standUp(gameSessionInfo, reqCmd);
//	}
//	
//	
//	public void stopAssistant(GameSessionInfo gameSessionInfo, ReqStopAssistantCmd reqCmd) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		gameSeat.stopAssistant(gameSessionInfo, reqCmd);
//	}
//	public void stopOnlooker(GameSessionInfo gameSessionInfo, ReqStopOnlookerCmd reqCmd) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		gameSeat.stopOnlooker(gameSessionInfo, reqCmd);
//	}
//	public void bootAssistant(GameSessionInfo gameSessionInfo, ReqBootAssistantCmd reqCmd) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		gameSeat.bootAssistant(gameSessionInfo, reqCmd);
//	}
//	public void bootOnlooker(GameSessionInfo gameSessionInfo, ReqBootOnlookerCmd reqCmd) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		gameSeat.bootOnlooker(gameSessionInfo, reqCmd);
//	}
//	
//	/***
//	 * 申请(下一轮)主席位继任人
//	 * @param player
//	 */
//	public final void applySeatSuccessor(GameSessionInfo gameSessionInfo, ReqApplySeatSuccessorCmd reqCmd) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		gameSeat.applySeatSuccessor(gameSessionInfo, reqCmd);
//	}
//	
//	/***
//	 * 设置(下一轮)主席位继任人
//	 * @param player
//	 */
//	public final void setSeatSuccessor(GameSessionInfo gameSessionInfo, ReqSetSeatSuccessorCmd reqCmd) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		gameSeat.setSeatSuccessor(gameSessionInfo, reqCmd);
//	}
//	
//	/***
//	 * 强制席中的某人站起
//	 * @param masterId
//	 * @param playerId
//	 */
//	public void forceStandUp(GameSessionInfo gameSessionInfo, ReqForceStandUpCmd reqCmd) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		gameSeat.forceStandUp(gameSessionInfo, reqCmd);
//	}
//	
//	/***
//	 * 在席位中对某个席位进行打赏
//	 * @param player
//	 */
//	public void reward(GameSessionInfo gameSessionInfo, ReqRewardCmd reqCmd) {
//		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
//		RewardPlugin rewardPlugin = (RewardPlugin)tableDesk.getPlugin(RewardPlugin.PLUGIN_NAME);
//		rewardPlugin.reward(gameSessionInfo, reqCmd);
//	}
//	
//	
//	/***聊天****/
//	public void chat(GameSessionInfo gameSessionInfo, ReqChatMultiCmd reqCmd) {
//		if(reqCmd.getPositionList() == null || reqCmd.getPositionList().size() == 0) {
//			throw new BizException("席位不能为空");
//		}
//		
//		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
//		ChatPlugin chatPlugin = (ChatPlugin)tableDesk.getPlugin(ChatPlugin.PLUGIN_NAME);
//		chatPlugin.chat(gameSessionInfo, reqCmd);
//	}
//	
//
//	/***申请直播****/
//	public void applyBroadcastLive(GameSessionInfo gameSessionInfo, ReqApplyBroadcastLiveCmd reqCmd) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		BroadcastLivePlugin broadcastLivePlugin = (BroadcastLivePlugin)gameSeat.getPlugin(BroadcastLivePlugin.PLUGIN_NAME);
//		broadcastLivePlugin.applyBroadcastLive(gameSessionInfo, reqCmd);
//	}
//	/***取消直播****/
//	public void cancleBroadcastLive(GameSessionInfo gameSessionInfo, ReqCancleBroadcastLiveCmd reqCmd) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		BroadcastLivePlugin broadcastLivePlugin = (BroadcastLivePlugin)gameSeat.getPlugin(BroadcastLivePlugin.PLUGIN_NAME);
//		broadcastLivePlugin.cancleBroadcastLive(gameSessionInfo, reqCmd);
//	}
//	/***直播****/
//	public void broadcastLive(GameSessionInfo gameSessionInfo, ReqBroadcastLiveCmd reqCmd, byte[] data) {
//		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
//		BroadcastLivePlugin broadcastLivePlugin = (BroadcastLivePlugin)gameSeat.getPlugin(BroadcastLivePlugin.PLUGIN_NAME);
//		broadcastLivePlugin.broadcastLive(gameSessionInfo, reqCmd, data);
//	}
//	/***
//	 * 管理员同意XXX的直播
//	 * @param player
//	 * @param id
//	 */
//	public void approveBroadcastLive(GameSessionInfo gameSessionInfo, ReqApproveBroadcastLiveCmd reqCmd) {
//		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
//		tableDesk.approveBroadcastLive(gameSessionInfo, reqCmd);
//	}
//	
//	/***
//	 * 申请成为管理员
//	 * 
//	 * 当无管理员时第一个自动成功管理员
//	 * 
//	 * @param player
//	 * @param position
//	 */
//	public void applyManager(GameSessionInfo gameSessionInfo, ReqApplyManagerCmd reqCmd) {
//		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
//		tableDesk.applyManager(gameSessionInfo, reqCmd);
//	}
//	
//	/***
//	 * 更改管理员
//	 * @param player
//	 * @param playerId 为空时，表示无管理人员
//	 */
//	public void changeManager(GameSessionInfo gameSessionInfo, ReqChangeManagerCmd reqCmd) {
//		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
//		tableDesk.changeManager(gameSessionInfo, reqCmd);
//	}
//	
//
//	/***游戏暂停****/
//	public void pause(GameSessionInfo gameSessionInfo, ReqPauseCmd reqCmd) {
//		if(reqCmd.getSeconds() <= 0) {
//			throw new BizException("暂停时长不能小于或等于0");
//		}
//		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
//		tableDesk.pause(gameSessionInfo, reqCmd);
//	}
//	
//	/***游戏取消暂停(恢复正常)****/
//	public void resume(GameSessionInfo gameSessionInfo, ReqResumeCmd reqCmd) {
//		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
//		tableDesk.resume(gameSessionInfo, reqCmd);
//	}
//	
//}
