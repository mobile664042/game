package com.simple.game.ddz.domain.dto;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.rtn.seat.RtnGameSeatInfoCmd;
import com.simple.game.core.domain.dto.GameSeat;
import com.simple.game.core.domain.dto.GameSessionInfo;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.SeatPlayer;
import com.simple.game.core.domain.dto.constant.SeatPost;
import com.simple.game.core.exception.BizException;
import com.simple.game.ddz.domain.cmd.push.game.notify.NotifyDoubledCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushDoubleShowCardCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushDoubledCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushPlayCardCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushReadyNextCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushRobLandlordCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushSurrenderCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqDoubledShowCardCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqDoubledCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqPlayCardCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqReadyNextCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqRobLandlordCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqSurrenderCmd;
import com.simple.game.ddz.domain.cmd.rtn.seat.RtnDdzGameSeatCmd;
import com.simple.game.ddz.domain.cmd.rtn.seat.RtnDoubledCmd;
import com.simple.game.ddz.domain.cmd.rtn.seat.RtnDoubledShowCardCmd;
import com.simple.game.ddz.domain.cmd.rtn.seat.RtnPlayCardCmd;
import com.simple.game.ddz.domain.cmd.rtn.seat.RtnRobLandlordCmd;
import com.simple.game.ddz.domain.dto.DdzDesk.DoubledShowCardResult;
import com.simple.game.ddz.domain.dto.config.DdzDeskItem;
import com.simple.game.ddz.domain.dto.constant.ddz.GameProgress;
import com.simple.game.ddz.domain.ruler.DdzCard.PlayCardResult;
import com.simple.game.ddz.robot.DdzRobotListener;

import lombok.Getter;

/***
 * ?????????
 * 
 * @author zhibozhang
 *
 */
@Getter
public class DdzGameSeat extends GameSeat{ 
	private static Logger logger = LoggerFactory.getLogger(DdzGameSeat.class);

	public DdzGameSeat(DdzDesk desk, int position) {
		super(desk, position);
	}
	
	public DdzGameSeat(GameSeat gameSeat) {
		super(gameSeat.getDesk(), gameSeat.getPosition());
	}

	/***
	 * ????????????????????????
	 */
	private boolean ready = true;
	
	/***????????????????????????****/
	private int skipCount;
	
	/***????????????????????????****/
	private int timeoutCount;
	
	public void skipCountIncrease() {
		skipCount ++;
	}
	public void timeoutCountIncrease() {
		skipCount ++;
		timeoutCount ++;
	}
	
	/***
	 * ??????????????????
	 */
	protected void preStandUp(SeatPlayer player) {
		if(!getDdzDesk().canStandUp() && player.getSeatPost() == SeatPost.master) {
			throw new BizException("????????????????????????????????????????????????(??????)");
		}
	}

	@Override
	protected void preSitdown(Player player) {
		//????????????????????????
		if(player.getBcoin() < ((DdzDeskItem)this.desk.getDeskItem()).getMinSitdownCoin()) {
			throw new BizException(String.format("%s????????????%s,?????????????????????", player.getId(), ((DdzDeskItem)this.desk.getDeskItem()).getMinSitdownCoin()));
		}
	}
	@Override
	protected void doSitdownMaster() {
		this.ready = true;
		logger.info("{}?????????????????????,????????????:{}--{}--{}", master.get().getPlayer().getNickname(), this.desk.getGameItem().getName(), this.desk.getAddrNo(), this.getPosition());
	}
	@Override
	protected void afterSitdownMaster(SeatPlayer seatPlayer) {
		DdzRobotListener.submitEvent(seatPlayer);
	}
	
	@Override
	public RtnGameSeatInfoCmd getGameSeatInfo() {
		RtnDdzGameSeatCmd rtnCmd = new RtnDdzGameSeatCmd();
		rtnCmd.copy(super.getGameSeatInfo());
		rtnCmd.setReady(ready);
		List<Integer> cards = this.getDdzDesk().getResidueCard(position);
		rtnCmd.setCards(cards);
		rtnCmd.setLeftSecond(this.getDdzDesk().getLeftSecond());
		rtnCmd.setSkipCount(skipCount);
		rtnCmd.setTimeoutCount(timeoutCount);
		return rtnCmd;
	}
	
	protected void clear() {
		super.clear();
		skipCount = 0;
		timeoutCount = 0;
	}
	
	/***
	 * ??????????????????
	 * @return
	 */
	public boolean isDiconnectPlayCard() {
		if(this.master.get() == null) {
			logger.warn("???????????????????????????????????????????????????bug!");
			return false;
		}
		
		if(!this.master.get().getPlayer().getOnline().isOnline() && assistantMap.size() == 0) {
			return true;
		}
		return false;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	/***
	 * ?????????????????????????????????
	 * @param playerId
	 * @param position
	 */
	public void readyNext(GameSessionInfo gameSessionInfo, ReqReadyNextCmd reqCmd) {
		if(getDdzDesk().getCurrentProgress() != GameProgress.ready) {
			throw new BizException("???????????????????????????????????????");
		}
		checkSeatPlayer(gameSessionInfo);
		
		//????????????????????????
		if(master.get().getPlayer().getBcoin() < ((DdzDeskItem)this.desk.getDeskItem()).getMinReadyCoin()) {
			throw new BizException(String.format("%s????????????%s,?????????????????????", master.get().getPlayer().getId(), ((DdzDeskItem)this.desk.getDeskItem()).getMinReadyCoin()));
		}
		this.ready = true;
		PushReadyNextCmd pushCmd = new PushReadyNextCmd();
		pushCmd.setPosition(position);
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}
	
	/***
	 * ?????????
	 * @param playerId
	 * @param position
	 * @param score		???????????????????????????
	 */
	public void robLandlord(GameSessionInfo gameSessionInfo, ReqRobLandlordCmd reqCmd) {
		SeatPlayer seatPlayer = checkSeatPlayer(gameSessionInfo);

		OutParam<List<Integer>> outParam = new OutParam<List<Integer>>();
		List<Integer> commonCards = getDdzDesk().robLandlord(position, reqCmd.getScore(), outParam);
		PushRobLandlordCmd pushCmd = reqCmd.valueOfPushRobLandlordCmd();
		pushCmd.setPosition(position);
		pushCmd.setCards(commonCards);
		
		//????????????
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
		
		RtnRobLandlordCmd rtnCmd = new RtnRobLandlordCmd();
		rtnCmd.setCards(commonCards);
		rtnCmd.setFinalCards(outParam.getParam());
		Player player = seatPlayer.getPlayer();
		player.getOnline().getSession().write(rtnCmd);
	}
	

	/***
	 * ??????
	 */
	public void doubled(GameSessionInfo gameSessionInfo, ReqDoubledCmd reqCmd) {
		SeatPlayer seatPlayer = checkSeatPlayer(gameSessionInfo);

		OutParam<Boolean> outParam = new OutParam<Boolean>();
		int doubleFinal = getDdzDesk().doubled(position, outParam);
		PushDoubledCmd pushCmd = reqCmd.valueOfPushDoubledCmd();
		pushCmd.setPosition(position);
		pushCmd.setDoubleFinal(doubleFinal);
		
		//????????????
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
		
		RtnDoubledCmd rtnCmd = new RtnDoubledCmd();
		rtnCmd.setDoubleFinal(doubleFinal);
		Player player = seatPlayer.getPlayer();
		
		//????????????
		if(outParam.getParam() != null && outParam.getParam()) {
			NotifyDoubledCmd nofifyCmd = new NotifyDoubledCmd();
			desk.broadcast(nofifyCmd, gameSessionInfo.getPlayerId());
			rtnCmd.setNext(true);		
		}
		
		player.getOnline().getSession().write(rtnCmd);
	}
	

	/***
	 * ??????
	 */
	public void doubledShowCard(GameSessionInfo gameSessionInfo, ReqDoubledShowCardCmd reqCmd) {
		SeatPlayer seatPlayer = checkSeatPlayer(gameSessionInfo);

		DoubledShowCardResult result = getDdzDesk().doubledShowCard(position);
		PushDoubleShowCardCmd pushCmd = reqCmd.valueOfPushShowCardCmd();
		pushCmd.setPosition(position);
		pushCmd.setCards(result.getCards());
		pushCmd.setDoubleFinal(result.getDoubleFinal());
		//????????????
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
		
		
		RtnDoubledShowCardCmd rtnCmd = new RtnDoubledShowCardCmd();
		rtnCmd.setDoubleFinal(result.getDoubleFinal());
		Player player = seatPlayer.getPlayer();
		
		//????????????
		if(result.isNext()) {
			NotifyDoubledCmd nofifyCmd = new NotifyDoubledCmd();
			desk.broadcast(nofifyCmd, gameSessionInfo.getPlayerId());
			rtnCmd.setNext(true);
		}
		player.getOnline().getSession().write(rtnCmd);
		
	}
	
	/***
	 * ??????
	 * @param playerId
	 * @param position
	 * @param cards
	 */
	public void playCard(GameSessionInfo gameSessionInfo, ReqPlayCardCmd reqCmd) {
		SeatPlayer seatPlayer = checkSeatPlayer(gameSessionInfo);
		PlayCardResult playCardResult = getDdzDesk().playCard(position, reqCmd.getCards());
		
		//????????????
		PushPlayCardCmd pushCmd = reqCmd.valueOfPushPlayCardCmd();
		pushCmd.setPosition(position);
		int finalDouble = getDdzDesk().getDoubleKind().getFinalDouble(playCardResult.getDoubleCount());
		pushCmd.setDoubleFinal(finalDouble);
		pushCmd.setResidueCount(playCardResult.getResidueCount());
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
		
		RtnPlayCardCmd rtnCmd = new RtnPlayCardCmd();
		Player player = seatPlayer.getPlayer();
		rtnCmd.setDoubleFinal(finalDouble);
		player.getOnline().getSession().write(rtnCmd);
	}
	
	/***
	 * ????????????
	 * ??????????????????
	 * ???????????????com.simple.game.ddz.domain.dto.config.DdzDeskItem.punishSurrenderDoubleCount
	 * @param playerId
	 * @param position
	 */
	public void surrender(GameSessionInfo gameSessionInfo, ReqSurrenderCmd reqCmd) {
		checkSeatPlayer(gameSessionInfo);
		getDdzDesk().surrender(position);
		
		PushSurrenderCmd pushCmd = reqCmd.valueOfPushSurrenderCmd();
		pushCmd.setPosition(position);
		
		//????????????
		this.broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}
	
	
	private SeatPlayer checkSeatPlayer(GameSessionInfo gameSessionInfo) {
		long playerId = gameSessionInfo.getPlayerId();
		GameSeat gameSeat = (GameSeat)gameSessionInfo.getAddress();
		if(gameSeat.getMaster().get() == null) {
			throw new BizException("???????????????????????????"); 
		}
		if(gameSeat.getMaster().get().getPlayer().getId() == playerId) {
			return gameSeat.getMaster().get(); 
		}
		SeatPlayer seatPlayer = gameSeat.getAssistantMap().get(playerId);
		if(seatPlayer == null) {
			throw new BizException("????????????????????????");
		}
		return seatPlayer;
	}
	public DdzDesk getDdzDesk() {
		return (DdzDesk)desk;
	}
}
