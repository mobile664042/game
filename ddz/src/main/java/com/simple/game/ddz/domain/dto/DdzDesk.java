package com.simple.game.ddz.domain.dto;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.push.game.PushLeftCmd;
import com.simple.game.core.domain.cmd.rtn.game.RtnGameInfoCmd;
import com.simple.game.core.domain.dto.GameSeat;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.SeatPlayer;
import com.simple.game.core.domain.dto.TableDesk;
import com.simple.game.core.domain.manager.CoinManager;
import com.simple.game.core.exception.BizException;
import com.simple.game.ddz.domain.cmd.push.seat.PushPlayCardCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushReadyNextCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushSurrenderCmd;
import com.simple.game.ddz.domain.dto.config.DdzDeskItem;
import com.simple.game.ddz.domain.dto.config.DdzGameItem;
import com.simple.game.ddz.domain.dto.constant.ddz.DoubleKind;
import com.simple.game.ddz.domain.dto.constant.ddz.GameProgress;
import com.simple.game.ddz.domain.good.DdzGame;
import com.simple.game.ddz.domain.manager.GameResultRecord;
import com.simple.game.ddz.domain.manager.GameResultRecord.ResultItem;
import com.simple.game.ddz.domain.manager.ResultManager;
import com.simple.game.ddz.domain.ruler.DdzCard;

import lombok.Getter;
import lombok.ToString;

/***
 * 斗地主的游戏桌
 * 
 * @author zhibozhang
 *
 */
@ToString
@Getter
public class DdzDesk extends TableDesk{
	private static Logger logger = LoggerFactory.getLogger(DdzDesk.class);
	
	protected final DdzCard ddzCard = new DdzCard();
	
	public DdzDesk(DdzGame game) {
		super(game);
	} 

	/***是否在进行中****/
	protected GameProgress currentProgress = GameProgress.ready;
	
	/***投降位***/
	protected int surrenderPosition;
	
	/***最近的游戏结束时间***/
	protected long lastGameOverTime;
	/***最近系统发牌时间***/
	protected long lastSendCardTime;
	
	/***最近一次过牌时间***/
	protected long lastPlayCardTime;
	
	public synchronized boolean onScan() {
		if(currentProgress == GameProgress.ready) {
			if(this.currentGame.getOnlineCount() == 0) {
				return false;
			}
			
			int readyCount = 0;
			handleDisconnectPlayer();
			for(int position = currentGame.getDeskItem().getMinPosition(); position <= currentGame.getDeskItem().getMinPosition(); position++) {
				DdzGameSeat gameSeat = (DdzGameSeat)this.seatPlayingMap.get(position);
				if(gameSeat == null) {
					logger.error("系统有重大bug!!! 席位没找到");
					return false;
				}
				
				if(!gameSeat.isReady() && gameSeat.getFansCount() > 0) {
					//还没准备好了
					forceStandUpPosition(gameSeat);
				}
				
				if(!gameSeat.isReady()) {
					return false;
				}
				readyCount++;
			}
			if(readyCount <= 3) {
				return false;
			}
			
			//更换主席位
			for(int position = currentGame.getDeskItem().getMinPosition(); position <= currentGame.getDeskItem().getMinPosition(); position++) {
				DdzGameSeat gameSeat = (DdzGameSeat)this.seatPlayingMap.get(position);
				gameSeat.handleChangeMaster();
			}
			
			//可以开始了
			//1.洗牌，
			shuffleCards();
			//2.发牌，3.出牌
			sendCards();
			currentProgress = GameProgress.sended;		
		}
		else if(currentProgress == GameProgress.sended) {
			//判断等待抢地主是否超时
			handleWaitRobLandlordTimeout();
		}
		else if(currentProgress == GameProgress.robbedLandlord) {
			//判断等待出牌是否超时
			handleWaitPlayCardTimeout();
		}
		else if(currentProgress == GameProgress.gameover || currentProgress == GameProgress.surrender ) {
			//结算游戏
			this.settle();
		}
		return false;
	}
	
	/***
	 * 处理掉线的用户
	 */
	private void handleDisconnectPlayer() {
		for(Player player : this.getTableGame().getOfflineMap().values()) {
			long time = System.currentTimeMillis() - player.getOnline().getDisconnectTime();
			if(time == 0 || time / 1000 < this.getDdzGameItem().getMaxDisconnectSecond()) {
				continue;
			}
			
			//判断是否是主席位
			boolean isMaster = false;
			for(GameSeat gameSeat : seatPlayingMap.values()) {
				if(gameSeat.getMaster().get() != null && gameSeat.getMaster().get().getPlayer().getId() != player.getId()) {
					isMaster = true;
					break;
				}
			}
			if(isMaster) {
				continue;
			}
			
			//强制下线
			OutParam<Player> outParam = OutParam.build();
			this.getTableGame().left(player.getId(), outParam);
			PushLeftCmd pushCmd = new PushLeftCmd();
			//TODO 
			//pushCmd.setPlayKind(playKind);
			pushCmd.setDeskNo(this.getDeskNo());
			pushCmd.setPlayerId(player.getId());
			this.getTableGame().broadcast(pushCmd, true);
		}
	}
	
	private boolean handleWaitRobLandlordTimeout() {
		long time = System.currentTimeMillis() - lastSendCardTime; 
		if(time == 0 || time / 1000 < this.getDdzGameItem().getMaxRobbedLandlordSecond()) {
			return false;
		}
		
		//超时，直接进入下一轮
		handleNext();
		return true;
	}
	
	/***
	 * 处理出牌超时的
	 * @return
	 */
	private boolean handleWaitPlayCardTimeout() {
		//当前出牌位
		long time = System.currentTimeMillis() - lastPlayCardTime; 
		if(time == 0) {
			return false;
		}
		long second = time / 1000; 
		if(second < this.getDdzGameItem().getDisconnectPlayCardSecond()) {
			return false;
		}
		
		int position = this.ddzCard.getCurrentPosition(); 
		//判断主席位是否掉线且助手为0
		DdzGameSeat gameSeat = (DdzGameSeat)this.getGameSeat(position);
		if(!gameSeat.isDiconnectPlayCard()) {
			//是否超时出牌
			if(second < this.getDdzGameItem().getMaxPlayCardSecond()) {
				return false;
			}
			else {
				gameSeat.timeoutCountIncrease();
			}
		}
		else {
			gameSeat.skipCountIncrease();
		}
		
		//自动过牌
		List<Integer> outCards = new ArrayList<Integer>(1); 
		boolean isGameOver = this.ddzCard.autoPlayCard(outCards);
		afterPlayCard(isGameOver);
		
		//广播
		PushPlayCardCmd pushCmd = new PushPlayCardCmd();
		pushCmd.setPosition(position);
		pushCmd.getCards().addAll(outCards);
		this.getTableGame().broadcast(pushCmd, true);
		return true;
	}
	
	/****
	 * 清理需要强制站起的用户
	 */
	private boolean forceStandUpPosition(DdzGameSeat gameSeat) {
		long time = System.currentTimeMillis() - lastGameOverTime; 
		if(time != 0 && time / 1000 > this.getDdzGameItem().getMaxReadyNextSecond()) {
			//强制踢出这些站着茅坑不拉屎的人
			gameSeat.standupAll();
			logger.info("游戏结束后等待{}毫秒，{}席位一直不进行准备状态，强制站起", time, gameSeat.getPosition());
		}
		
		//强制踢出那些长时间掉线的主席位
		if(!gameSeat.getMaster().get().getPlayer().getOnline().isOnline()) {
			//判断掉线是否超时
			time = System.currentTimeMillis() - gameSeat.getMaster().get().getPlayer().getOnline().getDisconnectTime(); 
			if(time != 0 && time / 1000 > this.getDdzGameItem().getMaxMasterDisconnectSecond()) {
				gameSeat.standupAll();
				logger.info("游戏结束后等待{}毫秒，{}主席位长时间掉线，强制站起", time, gameSeat.getPosition());
			}
		}
		
		//强制踢出那些经常出牌超时的人
		if(gameSeat.getTimeoutCount() > this.getDdzGameItem().getMaxPlayCardOuttimeCount()) {
			gameSeat.standupAll();
			logger.info("游戏结束后等待{}毫秒，{}主席位经常牌超时的，强制站起", time, gameSeat.getPosition());
		}
		
		//判断是否是经常跳过出牌
		if(gameSeat.getSkipCount() > this.getDdzGameItem().getMaxSkipCount()) {
			gameSeat.standupAll();
			logger.info("游戏结束后等待{}毫秒，{}主席位经常自动跳过的，强制站起", time, gameSeat.getPosition());
		}
		return true;
	}
	
	public boolean canStandUpMaster() {
		return currentProgress == GameProgress.ready;
	}
	private void shuffleCards() {
		this.ddzCard.shuffleCards();
	}
	private void sendCards() {
		this.ddzCard.sendCards();
		//发送到客户端
		{
			GameSeat gameSeat = this.seatPlayingMap.get(1);
			PushPlayCardCmd pushCmd = new PushPlayCardCmd();
			pushCmd.setPosition(1);
			pushCmd.getCards().addAll(this.ddzCard.getFirstCards());
			gameSeat.broadcast(pushCmd);
		}
		{
			GameSeat gameSeat = this.seatPlayingMap.get(2);
			PushPlayCardCmd pushCmd = new PushPlayCardCmd();
			pushCmd.setPosition(1);
			pushCmd.getCards().addAll(this.ddzCard.getSecondCards());
			gameSeat.broadcast(pushCmd);
		}
		{
			GameSeat gameSeat = this.seatPlayingMap.get(3);
			PushPlayCardCmd pushCmd = new PushPlayCardCmd();
			pushCmd.setPosition(1);
			pushCmd.getCards().addAll(this.ddzCard.getThirdCards());
			gameSeat.broadcast(pushCmd);
		}
		lastSendCardTime = System.currentTimeMillis();
	}
	

	protected Player buildGamePlayer(Player player) {
		return new DdzPlayer(player);
	}
	
	/***
	 * 抢地主
	 * @param playerId
	 * @param position
	 * @param score		简化操作，暂时不用
	 */
	public synchronized void robLandlord(long playerId, int position, int score, OutParam<SeatPlayer> outParam) {
		if(currentProgress != GameProgress.sended) {
			throw new BizException("不是发完牌状态，无法进行抢地主");
		}
		
		SeatPlayer seatPlayer = checkSeatPlayer(playerId, position);
		this.ddzCard.setLandlord(position);
		currentProgress = GameProgress.robbedLandlord;
		
		outParam.setParam(seatPlayer);
	}
	
	
	public synchronized PushPlayCardCmd playCard(long playerId, int position, List<Integer> cards, OutParam<SeatPlayer> outParam) {
		if(currentProgress != GameProgress.robbedLandlord) {
			throw new BizException("不是抢完状态，无法进行出牌");
		}
		
		SeatPlayer seatPlayer = checkSeatPlayer(playerId, position);
		boolean isGameOver = this.ddzCard.playCard(position, cards);
		afterPlayCard(isGameOver);
		outParam.setParam(seatPlayer);
		PushPlayCardCmd pushCmd = new PushPlayCardCmd();
		pushCmd.setPosition(position);
		pushCmd.getCards().addAll(cards);
		return pushCmd;
	}
	
	private void afterPlayCard(boolean isGameOver) {
		if(isGameOver) {
			//进入gameover状态了
			currentProgress = GameProgress.gameover;			
		}
		this.lastPlayCardTime = System.currentTimeMillis();
	}
	

	public synchronized PushReadyNextCmd readyNext(long playerId, int position, OutParam<SeatPlayer> outParam) {
		if(currentProgress != GameProgress.ready) {
			throw new BizException("不是准备状态，无法进行出牌");
		}
		
		//判断游戏币是否允足，能否满足下一轮开始
		SeatPlayer seatPlayer = checkSeatPlayer(playerId, position);
		outParam.setParam(seatPlayer);
		DdzGameSeat extGameSeat = (DdzGameSeat)seatPlayer.getGameSeat();
		PushReadyNextCmd pushCmd = extGameSeat.readyNext();
		pushCmd.setPosition(position);
		return pushCmd;
	}
	
	/***
	 * 投降认输
	 * 直接参考@com.simple.game.ddz.domain.dto.config.DdzGameItem.punishSurrenderDoubleCount处理
	 * @param playerId
	 * @param position
	 */
	public synchronized PushSurrenderCmd surrender(long playerId, int position, OutParam<SeatPlayer> outParam) {
		if(currentProgress != GameProgress.robbedLandlord) {
			throw new BizException("不是抢完状态，无法进行投降");
		}
		
		this.checkSeatPlayer(playerId, position);
		
		//进入surrender状态了
		currentProgress = GameProgress.surrender;			
		surrenderPosition = position;
		
		PushSurrenderCmd pushCmd = new PushSurrenderCmd();
		pushCmd.setPosition(position);
		return pushCmd;
	}
	

	protected RtnGameInfoCmd getRtnGameInfoCmd() {
		//TODO 
		return new RtnGameInfoCmd();
	}
	
	
	/***
	 * 游戏结算
	 */
	private void settle() {
		if(currentProgress != GameProgress.gameover && currentProgress != GameProgress.surrender) {
			logger.error("游戏出现严重的bug，状态不对就调用了");
			throw new BizException("游戏还未结束");
		}
		
		GameResultRecord gameResultRecord = null;
		if(currentProgress == GameProgress.gameover) {
			handleNormalResult();
		}
		else if(currentProgress == GameProgress.surrender) {
			handleSurrenderResult();
		}
		
		//处理逃跑的人
		handleEscapeResult(gameResultRecord);
		
		//游戏进入下一轮
		handleNext();
		//发送广播
	}
	private void handleNext() {
		currentProgress = GameProgress.ready;
		surrenderPosition = 0;
		lastGameOverTime = System.currentTimeMillis();
		((DdzGameSeat)this.seatPlayingMap.get(1)).setReady(false);
		((DdzGameSeat)this.seatPlayingMap.get(2)).setReady(false);
		((DdzGameSeat)this.seatPlayingMap.get(3)).setReady(false);
	}
	
	private void handleEscapeResult(GameResultRecord record) {
		if(record == null) {
			return;
		}
		
		for(int position = currentGame.getDeskItem().getMinPosition(); position <= currentGame.getDeskItem().getMinPosition(); position++) {
			DdzGameSeat gameSeat = (DdzGameSeat)this.seatPlayingMap.get(position);
			if(gameSeat.getSkipCount() <= this.getDdzGameItem().getEscape2SkipCount()) {
				continue;
			}
			
			//要处罚了(系统一份，另外两个玩家各一份)
			CoinManager.changeCoin(gameSeat.getMaster().get().getPlayer(), -record.getSingleResult()*3, record.getBatchNo(), "逃跑处罚！");
			
			String reason = String.format("%s号位，逃跑处罚！", position);
			if(position == 1) {
				CoinManager.changeCoin(this.seatPlayingMap.get(2).getMaster().get().getPlayer(), record.getSingleResult(), record.getBatchNo(), reason);
				CoinManager.changeCoin(this.seatPlayingMap.get(3).getMaster().get().getPlayer(), record.getSingleResult(), record.getBatchNo(), reason);
			}
			else if(position == 2) {
				CoinManager.changeCoin(this.seatPlayingMap.get(1).getMaster().get().getPlayer(), record.getSingleResult(), record.getBatchNo(), reason);
				CoinManager.changeCoin(this.seatPlayingMap.get(3).getMaster().get().getPlayer(), record.getSingleResult(), record.getBatchNo(), reason);
			}
			else {
				CoinManager.changeCoin(this.seatPlayingMap.get(1).getMaster().get().getPlayer(), record.getSingleResult(), record.getBatchNo(), reason);
				CoinManager.changeCoin(this.seatPlayingMap.get(2).getMaster().get().getPlayer(), record.getSingleResult(), record.getBatchNo(), reason);
			}
		}
	}
	
	private void handleNormalResult() {
		int doubleCount = this.ddzCard.getDoubleCount();
		if(this.ddzCard.isSpring()) {
			doubleCount += 1;
		}
		long singleResult = this.getDoubleKind().calcResult(this.getDdzDeskItem().getUnitPrice(), doubleCount);
		int landlordPosition = this.ddzCard.getLandlordPosition();
		boolean landlordWin = this.ddzCard.isLandlordWin();
		
		GameResultRecord gameResultRecord = new GameResultRecord();
		gameResultRecord.setUnitPrice(this.getDdzDeskItem().getUnitPrice());
		gameResultRecord.setDoubleCount(doubleCount);
		gameResultRecord.setSingleResult(singleResult);
		gameResultRecord.setDoubleKind(getDoubleKind());
		gameResultRecord.setLandlordPosition(landlordPosition);
		gameResultRecord.getCards().addAll(this.ddzCard.getAllCards());
		
		if(landlordPosition == 1) {
			{
				Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
				long coin = landlordWin ? (singleResult * 2) : -(singleResult * 2);
				String reason = landlordWin ? "当地主赢了" : "当地主输了";
				CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
			}
			{
				Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
				long coin = landlordWin ? (singleResult) : -(singleResult);
				String reason = landlordWin ? "当农民输了" : "当农民赢了";
				CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
			}
			{
				Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
				long coin = landlordWin ? (singleResult) : -(singleResult);
				String reason = landlordWin ? "当农民输了" : "当农民赢了";
				CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
			}
		}
		else if(landlordPosition == 2) {
			{
				Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
				long coin = landlordWin ? (singleResult * 2) : -(singleResult * 2);
				String reason = landlordWin ? "当地主赢了" : "当地主输了";
				CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
			}
			{
				Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
				long coin = landlordWin ? (singleResult) : -(singleResult);
				String reason = landlordWin ? "当农民输了" : "当农民赢了";
				CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
			}
			{
				Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
				long coin = landlordWin ? (singleResult) : -(singleResult);
				String reason = landlordWin ? "当农民输了" : "当农民赢了";
				CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
			}
		}
		else{
			{
				Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
				long coin = landlordWin ? (singleResult * 2) : -(singleResult * 2);
				String reason = landlordWin ? "当地主赢了" : "当地主输了";
				CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
			}
			{
				Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
				long coin = landlordWin ? (singleResult) : -(singleResult);
				String reason = landlordWin ? "当农民输了" : "当农民赢了";
				CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
			}
			{
				Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
				long coin = landlordWin ? (singleResult) : -(singleResult);
				String reason = landlordWin ? "当农民输了" : "当农民赢了";
				CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
			}
		}
		ResultManager.save(gameResultRecord);
	}
	
	/***
	 * 投降者赔偿当前的损失，并且加倍偿还
	 * @return
	 */
	private GameResultRecord handleSurrenderResult() {
		int doubleCount = this.ddzCard.getDoubleCount() + this.getDdzGameItem().getPunishSurrenderDoubleCount();
		if(this.ddzCard.isSpring()) {
			doubleCount += 1;
		}
		long singleResult = this.getDoubleKind().calcResult(this.getDdzDeskItem().getUnitPrice(), doubleCount);
		int landlordPosition = this.ddzCard.getLandlordPosition();
		boolean landlordWin = (landlordPosition != this.surrenderPosition);
		
		GameResultRecord gameResultRecord = new GameResultRecord();
		gameResultRecord.setUnitPrice(this.getDdzDeskItem().getUnitPrice());
		gameResultRecord.setDoubleCount(doubleCount);
		gameResultRecord.setDoubleKind(getDoubleKind());
		gameResultRecord.setLandlordPosition(landlordPosition);
		gameResultRecord.getCards().addAll(this.ddzCard.getAllCards());
		
		if(landlordWin) {
			if(landlordPosition == 1) {
				{
					Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
					long coin = (singleResult * 2);
					CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "当地主赢了,对方有投降");
					gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
				}
				{
					Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
					if(surrenderPosition == 2) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "当农民输了,我投降了");
						gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
					}
				}
				{
					Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
					if(surrenderPosition == 3) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "当农民输了,我投降了");
						gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
					}
				}
			}
			else if(landlordPosition == 2) {
				{
					Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
					long coin = (singleResult * 2);
					CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "当地主赢了,对方有投降");
					gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
				}
				{
					Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
					if(surrenderPosition == 1) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "当农民输了,我投降了");
						gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
					}
				}
				{
					Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
					if(surrenderPosition == 3) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "当农民输了,我投降了");
						gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
					}
				}
			}
			else{
				{
					Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
					long coin = (singleResult * 2);
					CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "当地主赢了,对方有投降");
					gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
				}
				{
					Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
					if(surrenderPosition == 1) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "当农民输了,我投降了");
						gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
					}
				}
				{
					Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
					if(surrenderPosition == 2) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "当农民输了,我投降了");
						gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
					}
				}
			}
		}
		
		
		else {
			//当地主输了
			if(landlordPosition == 1) {
				{
					Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
					long coin = -(singleResult * 2);
					CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "当地主输了,我投降了");
					gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
				}
				{
					Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "当农民赢了,对方有投降");
					gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
				}
				{
					Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "当农民赢了,对方有投降");
					gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
				}
			}
			else if(landlordPosition == 2) {
				{
					Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
					long coin = -(singleResult * 2);
					CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "当地主输了,我投降了");
					gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
				}
				{
					Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "当农民赢了,对方有投降");
					gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
				}
				{
					Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "当农民赢了,对方有投降");
					gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
				}
			}
			else{
				{
					Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
					long coin = -(singleResult * 2);
					CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "当地主输了,我投降了");
					gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
				}
				{
					Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "当农民赢了,对方有投降");
					gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
				}
				{
					Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "当农民赢了,对方有投降");
					gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
				}
			}
		}
		ResultManager.save(gameResultRecord);
		return gameResultRecord;
	}
	
	private SeatPlayer checkSeatPlayer(long playerId, int position) {
		GameSeat gameSeat = this.seatPlayingMap.get(position);
		if(gameSeat == null) {
			throw new BizException("无效的请求");
		}
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
	
	
	
	protected DoubleKind getDoubleKind() {
		DdzGameItem config = getDdzGameItem();
		if(config.getDoubleKind() != null ) {
			return config.getDoubleKind();
		}
		return DoubleKind.exponential;
	}
	protected DdzGameItem getDdzGameItem() {
		return (DdzGameItem)this.getTableGame().getGameItem();
	}
	protected DdzDeskItem getDdzDeskItem() {
		return (DdzDeskItem)this.getTableGame().getDeskItem();
	}
	@Override
	protected GameSeat buildGameSeat(int position){
		return new DdzGameSeat(this, position);
	}
	
}
