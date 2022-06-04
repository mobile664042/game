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
import com.simple.game.ddz.domain.cmd.push.seat.PushPlayCardCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushReadyNextCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushRobLandlordCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushSurrenderCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqPlayCardCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqReadyNextCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqRobLandlordCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqSurrenderCmd;
import com.simple.game.ddz.domain.cmd.rtn.seat.RtnDdzGameSeatCmd;
import com.simple.game.ddz.domain.cmd.rtn.seat.RtnPlayCardCmd;
import com.simple.game.ddz.domain.cmd.rtn.seat.RtnRobLandlordCmd;
import com.simple.game.ddz.domain.dto.config.DdzDeskItem;
import com.simple.game.ddz.domain.dto.constant.ddz.GameProgress;
import com.simple.game.ddz.domain.ruler.DdzCard.PlayCardResult;
import com.simple.game.ddz.robot.DdzRobotListener;

import lombok.Getter;

/***
 * 游戏桌
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
	 * 是否准备了好没有
	 */
	private boolean ready = true;
	
	/***当前轮的跳过次数****/
	private int skipCount;
	
	/***当前轮的跳过次数****/
	private int timeoutCount;
	
	public void skipCountIncrease() {
		skipCount ++;
	}
	public void timeoutCountIncrease() {
		skipCount ++;
		timeoutCount ++;
	}
	
	/***
	 * 主席位想逃跑
	 */
	protected void preStandUp(SeatPlayer player) {
		if(!getDdzDesk().canStandUp() && player.getSeatPost() == SeatPost.master) {
			throw new BizException("游戏正在进行中，主席位不可以站起(离开)");
		}
	}

	@Override
	protected void preSitdown(Player player) {
		//判断游戏币够不够
		if(player.getBcoin() < ((DdzDeskItem)this.desk.getDeskItem()).getMinSitdownCoin()) {
			throw new BizException(String.format("%s的钱不够%s,无法坐下主席位", player.getId(), ((DdzDeskItem)this.desk.getDeskItem()).getMinSitdownCoin()));
		}
	}
	@Override
	protected void doSitdownMaster() {
		this.ready = true;
		logger.info("{}已自动准备好了,所在席位:{}--{}--{}", master.get().getPlayer().getNickname(), this.desk.getGameItem().getName(), this.desk.getAddrNo(), this.getPosition());
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
	 * 是否掉线出牌
	 * @return
	 */
	public boolean isDiconnectPlayCard() {
		if(this.master.get() == null) {
			logger.warn("判断是否掉线时，主席位为空，是否有bug!");
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
	 * 当前轮结束，准备下一轮
	 * @param playerId
	 * @param position
	 */
	public void readyNext(GameSessionInfo gameSessionInfo, ReqReadyNextCmd reqCmd) {
		if(getDdzDesk().getCurrentProgress() != GameProgress.ready) {
			throw new BizException("不是准备状态，无法进行出牌");
		}
		checkSeatPlayer(gameSessionInfo);
		
		//判断游戏币够不够
		if(master.get().getPlayer().getBcoin() < ((DdzDeskItem)this.desk.getDeskItem()).getMinReadyCoin()) {
			throw new BizException(String.format("%s的钱不够%s,无法准备下一轮", master.get().getPlayer().getId(), ((DdzDeskItem)this.desk.getDeskItem()).getMinReadyCoin()));
		}
		this.ready = true;
		PushReadyNextCmd pushCmd = new PushReadyNextCmd();
		pushCmd.setPosition(position);
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}
	
	/***
	 * 抢地主
	 * @param playerId
	 * @param position
	 * @param score		简化操作，暂时不用
	 */
	public void robLandlord(GameSessionInfo gameSessionInfo, ReqRobLandlordCmd reqCmd) {
		SeatPlayer seatPlayer = checkSeatPlayer(gameSessionInfo);

		OutParam<List<Integer>> outParam = new OutParam<List<Integer>>();
		List<Integer> commonCards = getDdzDesk().robLandlord(position, reqCmd.getScore(), outParam);
		PushRobLandlordCmd pushCmd = reqCmd.valueOfPushRobLandlordCmd();
		pushCmd.setPosition(position);
		pushCmd.setCards(commonCards);
		
		//发送广播
		desk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
		
		RtnRobLandlordCmd rtnCmd = new RtnRobLandlordCmd();
		rtnCmd.setCards(commonCards);
		rtnCmd.setFinalCards(outParam.getParam());
		Player player = seatPlayer.getPlayer();
		player.getOnline().getSession().write(rtnCmd);
	}
	
	/***
	 * 过牌
	 * @param playerId
	 * @param position
	 * @param cards
	 */
	public void playCard(GameSessionInfo gameSessionInfo, ReqPlayCardCmd reqCmd) {
		SeatPlayer seatPlayer = checkSeatPlayer(gameSessionInfo);
		PlayCardResult playCardResult = getDdzDesk().playCard(position, reqCmd.getCards());
		
		//发送广播
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
	 * 投降认输
	 * 提前结束游戏
	 * 处理方式按com.simple.game.ddz.domain.dto.config.DdzDeskItem.punishSurrenderDoubleCount
	 * @param playerId
	 * @param position
	 */
	public void surrender(GameSessionInfo gameSessionInfo, ReqSurrenderCmd reqCmd) {
		checkSeatPlayer(gameSessionInfo);
		getDdzDesk().surrender(position);
		
		PushSurrenderCmd pushCmd = reqCmd.valueOfPushSurrenderCmd();
		pushCmd.setPosition(position);
		
		//发送广播
		this.broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}
	
	
	private SeatPlayer checkSeatPlayer(GameSessionInfo gameSessionInfo) {
		long playerId = gameSessionInfo.getPlayerId();
		GameSeat gameSeat = (GameSeat)gameSessionInfo.getAddress();
		if(gameSeat.getMaster().get() == null) {
			throw new BizException("主席位空缺不能操作"); 
		}
		if(gameSeat.getMaster().get().getPlayer().getId() == playerId) {
			return gameSeat.getMaster().get(); 
		}
		SeatPlayer seatPlayer = gameSeat.getAssistantMap().get(playerId);
		if(seatPlayer == null) {
			throw new BizException("旁观人员不能操作");
		}
		return seatPlayer;
	}
	public DdzDesk getDdzDesk() {
		return (DdzDesk)desk;
	}
}
