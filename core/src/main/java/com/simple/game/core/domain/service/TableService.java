package com.simple.game.core.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.constant.GameConstant;
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
import com.simple.game.core.domain.dto.AddressNo;
import com.simple.game.core.domain.dto.GameSeat;
import com.simple.game.core.domain.dto.GameSessionInfo;
import com.simple.game.core.domain.dto.TableDesk;
import com.simple.game.core.domain.dto.desk.ChatPlugin;
import com.simple.game.core.domain.dto.desk.RewardPlugin;
import com.simple.game.core.domain.dto.seat.BroadcastLivePlugin;
import com.simple.game.core.domain.manager.GameManager;
import com.simple.game.core.exception.BizException;
import com.simple.game.core.util.GameSession;

import lombok.Getter;

/***
 * ????????????????????????
 * 
 * ?????????????????????????????????
 * 
 * @author zhibozhang
 *
 */
@Getter
public abstract class TableService{
	private final static Logger logger = LoggerFactory.getLogger(TableService.class);
	/***
	 * ????????????
	 */
	protected final GameManager gameManager;
	
	public TableService(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public TableDesk checkAndGet(int playKind, int deskNo) {
		TableDesk tableDesk = gameManager.getTableDesk(playKind, deskNo);
		if(tableDesk == null) {
			throw new BizException("??????????????????! " + deskNo);
		}
		return tableDesk;
	}
	public TableDesk getTableDesk(AddressNo address) {
		if(address instanceof TableDesk) {
			return ((TableDesk)address);
		}
		else if(address instanceof GameSeat) {
			return ((GameSeat)address).getDesk();
		}
		logger.warn("address={}????????????", address);
		throw new BizException("address????????????!" + address.getAddrNo());
	}
	
	public TableDesk checkAndGetTableDesk(AddressNo address) {
		if(address instanceof TableDesk) {
			return ((TableDesk)address);
		}
		throw new BizException("???????????????");
	}
	
	public GameSeat checkAndGetGameSeat(AddressNo address) {
		if(address instanceof GameSeat) {
			return ((GameSeat)address);
		}
		throw new BizException("????????????");
	}


	/***
	 * ????????????
	 */
	public void join(ReqJoinCmd reqCmd, GameSession gameSession) {
    	TableDesk tableDesk = checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		tableDesk.join(reqCmd, gameSession);
	}
	
	
	
	/***
	 * ????????????????????????
	 */
	public void getOnlineList(GameSessionInfo gameSessionInfo, ReqGetOnlineListCmd reqCmd) {
		if(reqCmd.getFromPage() < 0) {
			throw new BizException("????????????0??????");
		}
		
		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
		tableDesk.getOnlineList(gameSessionInfo, reqCmd);
	}
	
	/***
	 * ????????????
	 */
	public void left(GameSessionInfo gameSessionInfo, ReqLeftCmd reqCmd) {
		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
		tableDesk.left(gameSessionInfo, reqCmd);
	}
	
	
	/***
	 * ????????????
	 */
	public void kickout(GameSessionInfo gameSessionInfo, ReqKickoutCmd reqCmd) {
		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
		tableDesk.kickout(gameSessionInfo, reqCmd);
	}
	
	/***??????****/
	public void chat(GameSessionInfo gameSessionInfo, ReqChatCmd reqCmd) {
		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
		ChatPlugin chatPlugin = (ChatPlugin)tableDesk.getPlugin(ChatPlugin.PLUGIN_NAME);
		chatPlugin.chat(gameSessionInfo, reqCmd);
	}
	
	/***???????????????***/
	public void disconnect(GameSessionInfo gameSessionInfo, ReqDisconnectCmd reqCmd) {
		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
		tableDesk.disconnect(gameSessionInfo, reqCmd);
	}
	
	/***????????????***/
	public void connected(GameSession gameSession, ReqConnectCmd reqCmd) {
		GameSessionInfo gameSessionInfo = (GameSessionInfo)gameSession.getAttachment().get(GameConstant.GAME_SESSION_INFO);
		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
		tableDesk.connect(gameSession, reqCmd);
	}
	
	
	
	
	
	
	
	
	
	/***
	 * ????????????????????????
	 * 
	 * @param player
	 * @param position
	 */
	public final void sitdown(GameSessionInfo gameSessionInfo, ReqSitdownCmd reqCmd) {
		TableDesk tableDesk = checkAndGetTableDesk(gameSessionInfo.getAddress());
		GameSeat gameSeat = tableDesk.getGameSeat(reqCmd.getPosition());
		if(gameSeat == null) {
			throw new BizException(String.format("%s???????????????", reqCmd.getPosition()));
		}
		gameSeat.sitdown(gameSessionInfo, reqCmd);
	}
	
	public void getSeatPlayerList(GameSessionInfo gameSessionInfo, ReqGetSeatPlayerListCmd reqCmd) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		gameSeat.getSeatPlayerList(gameSessionInfo, reqCmd);
	}
	
	public void getAssistantList(GameSessionInfo gameSessionInfo, ReqGetAssistantListCmd reqCmd) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		gameSeat.getAssistantList(gameSessionInfo, reqCmd);
	}

	/***
	 * ????????????
	 * 
	 * @param player
	 */
	public final void quickSitdown(GameSessionInfo gameSessionInfo, ReqQuickSitdownCmd reqCmd) {
		TableDesk tableDesk = checkAndGetTableDesk(gameSessionInfo.getAddress());
		GameSeat gameSeat = tableDesk.getIdelGameSeat();
		if(gameSeat == null) {
			throw new BizException(String.format("??????????????????????????????"));
		}
		gameSeat.sitdown(gameSessionInfo, reqCmd.valueOfReqSitdownCmd(gameSeat.getPosition()));
	}
	
	/***
	 * ????????????
	 * @param playerId
	 */
	public void applyAssistant(GameSessionInfo gameSessionInfo, ReqApplyAssistantCmd reqCmd) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		gameSeat.applyAssistant(gameSessionInfo, reqCmd);
	}
	
	/***
	 * ??????XXX????????????
	 * @param player
	 * @param id
	 */
	public void approveApplyAssistant(GameSessionInfo gameSessionInfo, ReqApproveApplyAssistantCmd reqCmd) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		gameSeat.approveApplyAssistant(gameSessionInfo, reqCmd);
	}
	
	/***
	 * ?????????????????????
	 * @param player
	 */
	public final void standUp(GameSessionInfo gameSessionInfo, ReqStandUpCmd reqCmd) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		gameSeat.standUp(gameSessionInfo, reqCmd);
	}
	
	
	public void stopAssistant(GameSessionInfo gameSessionInfo, ReqStopAssistantCmd reqCmd) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		gameSeat.stopAssistant(gameSessionInfo, reqCmd);
	}
	public void stopOnlooker(GameSessionInfo gameSessionInfo, ReqStopOnlookerCmd reqCmd) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		gameSeat.stopOnlooker(gameSessionInfo, reqCmd);
	}
	public void bootAssistant(GameSessionInfo gameSessionInfo, ReqBootAssistantCmd reqCmd) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		gameSeat.bootAssistant(gameSessionInfo, reqCmd);
	}
	public void bootOnlooker(GameSessionInfo gameSessionInfo, ReqBootOnlookerCmd reqCmd) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		gameSeat.bootOnlooker(gameSessionInfo, reqCmd);
	}
	
	/***
	 * ??????(?????????)??????????????????
	 * @param player
	 */
	public final void applySeatSuccessor(GameSessionInfo gameSessionInfo, ReqApplySeatSuccessorCmd reqCmd) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		gameSeat.applySeatSuccessor(gameSessionInfo, reqCmd);
	}
	
	/***
	 * ??????(?????????)??????????????????
	 * @param player
	 */
	public final void setSeatSuccessor(GameSessionInfo gameSessionInfo, ReqSetSeatSuccessorCmd reqCmd) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		gameSeat.setSeatSuccessor(gameSessionInfo, reqCmd);
	}
	
	/***
	 * ???????????????????????????
	 * @param masterId
	 * @param playerId
	 */
	public void forceStandUp(GameSessionInfo gameSessionInfo, ReqForceStandUpCmd reqCmd) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		gameSeat.forceStandUp(gameSessionInfo, reqCmd);
	}
	
	/***
	 * ???????????????????????????????????????
	 * @param player
	 */
	public void reward(GameSessionInfo gameSessionInfo, ReqRewardCmd reqCmd) {
		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
		RewardPlugin rewardPlugin = (RewardPlugin)tableDesk.getPlugin(RewardPlugin.PLUGIN_NAME);
		rewardPlugin.reward(gameSessionInfo, reqCmd);
	}
	
	
	/***??????****/
	public void chat(GameSessionInfo gameSessionInfo, ReqChatMultiCmd reqCmd) {
		if(reqCmd.getPositionList() == null || reqCmd.getPositionList().size() == 0) {
			throw new BizException("??????????????????");
		}
		
		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
		ChatPlugin chatPlugin = (ChatPlugin)tableDesk.getPlugin(ChatPlugin.PLUGIN_NAME);
		chatPlugin.chat(gameSessionInfo, reqCmd);
	}
	

	/***????????????****/
	public void applyBroadcastLive(GameSessionInfo gameSessionInfo, ReqApplyBroadcastLiveCmd reqCmd) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		BroadcastLivePlugin broadcastLivePlugin = (BroadcastLivePlugin)gameSeat.getPlugin(BroadcastLivePlugin.PLUGIN_NAME);
		broadcastLivePlugin.applyBroadcastLive(gameSessionInfo, reqCmd);
	}
	/***????????????****/
	public void cancleBroadcastLive(GameSessionInfo gameSessionInfo, ReqCancleBroadcastLiveCmd reqCmd) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		BroadcastLivePlugin broadcastLivePlugin = (BroadcastLivePlugin)gameSeat.getPlugin(BroadcastLivePlugin.PLUGIN_NAME);
		broadcastLivePlugin.cancleBroadcastLive(gameSessionInfo, reqCmd);
	}
	/***??????****/
	public void broadcastLive(GameSessionInfo gameSessionInfo, ReqBroadcastLiveCmd reqCmd, byte[] data) {
		GameSeat gameSeat = checkAndGetGameSeat(gameSessionInfo.getAddress());
		BroadcastLivePlugin broadcastLivePlugin = (BroadcastLivePlugin)gameSeat.getPlugin(BroadcastLivePlugin.PLUGIN_NAME);
		broadcastLivePlugin.broadcastLive(gameSessionInfo, reqCmd, data);
	}
	/***
	 * ???????????????XXX?????????
	 * @param player
	 * @param id
	 */
	public void approveBroadcastLive(GameSessionInfo gameSessionInfo, ReqApproveBroadcastLiveCmd reqCmd) {
		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
		tableDesk.approveBroadcastLive(gameSessionInfo, reqCmd);
	}
	
	/***
	 * ?????????????????????
	 * 
	 * ????????????????????????????????????????????????
	 * 
	 * @param player
	 * @param position
	 */
	public void applyManager(GameSessionInfo gameSessionInfo, ReqApplyManagerCmd reqCmd) {
		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
		tableDesk.applyManager(gameSessionInfo, reqCmd);
	}
	
	/***
	 * ???????????????
	 * @param player
	 * @param playerId ?????????????????????????????????
	 */
	public void changeManager(GameSessionInfo gameSessionInfo, ReqChangeManagerCmd reqCmd) {
		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
		tableDesk.changeManager(gameSessionInfo, reqCmd);
	}
	

	/***????????????****/
	public void pause(GameSessionInfo gameSessionInfo, ReqPauseCmd reqCmd) {
		if(reqCmd.getSeconds() <= 0) {
			throw new BizException("?????????????????????????????????0");
		}
		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
		tableDesk.pause(gameSessionInfo, reqCmd);
	}
	
	/***??????????????????(????????????)****/
	public void resume(GameSessionInfo gameSessionInfo, ReqResumeCmd reqCmd) {
		TableDesk tableDesk = getTableDesk(gameSessionInfo.getAddress());
		tableDesk.resume(gameSessionInfo, reqCmd);
	}

}
