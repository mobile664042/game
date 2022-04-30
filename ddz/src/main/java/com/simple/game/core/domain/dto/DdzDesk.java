package com.simple.game.core.domain.dto;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.push.ddz.PushPlayCardCmd;
import com.simple.game.core.domain.cmd.push.ddz.PushReadyNextCmd;
import com.simple.game.core.domain.cmd.push.ddz.PushRobLandlordCmd;
import com.simple.game.core.domain.cmd.push.ddz.PushSurrenderCmd;
import com.simple.game.core.domain.cmd.rtn.RtnGameInfoCmd;
import com.simple.game.core.domain.dto.config.DdzDeskItem;
import com.simple.game.core.domain.dto.config.DdzGameItem;
import com.simple.game.core.domain.dto.constant.ddz.DoubleKind;
import com.simple.game.core.domain.dto.constant.ddz.GameProgress;
import com.simple.game.core.domain.good.DdzGame;
import com.simple.game.core.domain.good.ddz.service.GameResultRecord;
import com.simple.game.core.domain.good.ddz.service.GameResultRecord.ResultItem;
import com.simple.game.core.domain.good.ddz.service.ResultManager;
import com.simple.game.core.domain.ruler.DdzCard;
import com.simple.game.core.domain.service.CoinManager;
import com.simple.game.core.exception.BizException;

import lombok.ToString;

/***
 * 斗地主的游戏桌
 * 
 * @author zhibozhang
 *
 */
@ToString
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
	

	public boolean onScan() {
		if(currentProgress == GameProgress.ready) {
			if(this.seatPlayingMap.size() != 3) {
				//未凑足3个人
				return false;
			}
			
			for(int position=1; position<=3; position++) {
				GameSeat gameSeat = this.seatPlayingMap.get(position);
				if(gameSeat == null) {
					logger.error("系统有重大bug!!! 席位没找到");
					return false;
				}
				if(gameSeat.getMaster() == null) {
					logger.debug("{}主席位没人敢上", position);
					return false;
				}
				DdzGameSeat extGameSeat = (DdzGameSeat)gameSeat;
				if(!extGameSeat.isReady()) {
					//还没准备好了
					return false;
				}
				
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
		}
		else if(currentProgress == GameProgress.robbedLandlord) {
			//判断等待出牌是否超时
			
		}
		else if(currentProgress == GameProgress.gameover || currentProgress == GameProgress.gameover ) {
			//结算游戏
			this.settle();
		}
		// TODO Auto-generated method stub
		return false;
	}
	
	public void shuffleCards() {
		this.ddzCard.shuffleCards();
	}
	public void sendCards() {
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
	public PushRobLandlordCmd robLandlord(long playerId, int position, int score, OutParam<SeatPlayer> outParam) {
		if(currentProgress != GameProgress.sended) {
			throw new BizException("不是发完牌状态，无法进行抢地主");
		}
		
		SeatPlayer seatPlayer = checkSeatPlayer(playerId, position);
		this.ddzCard.setLandlord(position);
		currentProgress = GameProgress.robbedLandlord;
		
		outParam.setParam(seatPlayer);
		PushRobLandlordCmd pushCmd = new PushRobLandlordCmd();
		pushCmd.setPosition(position);
		pushCmd.getCards().addAll(this.ddzCard.getCommonCards());
		return pushCmd;
	}
	
	
	public PushPlayCardCmd playCard(long playerId, int position, List<Integer> cards, OutParam<SeatPlayer> outParam) {
		if(currentProgress != GameProgress.robbedLandlord) {
			throw new BizException("不是抢完状态，无法进行出牌");
		}
		
		SeatPlayer seatPlayer = checkSeatPlayer(playerId, position);
		boolean isGameOver = this.ddzCard.playCard(position, cards);
		if(isGameOver) {
			//进入gameover状态了
			currentProgress = GameProgress.gameover;			
		}
		outParam.setParam(seatPlayer);
		PushPlayCardCmd pushCmd = new PushPlayCardCmd();
		pushCmd.setPosition(position);
		pushCmd.getCards().addAll(cards);
		return pushCmd;
	}
	

	public PushReadyNextCmd readyNext(long playerId, int position, OutParam<SeatPlayer> outParam) {
		if(currentProgress != GameProgress.ready) {
			throw new BizException("不是准备状态，无法进行出牌");
		}
		SeatPlayer seatPlayer = checkSeatPlayer(playerId, position);
		outParam.setParam(seatPlayer);
		DdzGameSeat extGameSeat = (DdzGameSeat)seatPlayer.getGameSeat();
		extGameSeat.setReady(true);
		
		PushReadyNextCmd pushCmd = new PushReadyNextCmd();
		pushCmd.setPosition(position);
		return pushCmd;
	}
	
	/***
	 * 投降认输
	 * 直接参考@com.simple.game.core.domain.dto.config.ddz.ExtGameItem.punishSurrenderDoubleCount处理
	 * @param playerId
	 * @param position
	 */
	public PushSurrenderCmd surrender(long playerId, int position, OutParam<SeatPlayer> outParam) {
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
	public void settle() {
		if(currentProgress != GameProgress.gameover || currentProgress != GameProgress.surrender) {
			logger.error("游戏出现严重的bug，状态不对就调用了");
			throw new BizException("游戏还未结束");
		}
		
		if(currentProgress != GameProgress.gameover) {
			handleNormalResult();
		}
		else if(currentProgress != GameProgress.surrender) {
			handleSurrenderResult();
		}
		
		
		//TODO 判断座位上的游戏币是否还是足够
		//TODO 判断是否要强制踢某些人下些
		
		
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
		gameResultRecord.setDoubleKind(getDoubleKind());
		gameResultRecord.setLandlordPosition(landlordPosition);
		gameResultRecord.getCards().addAll(this.ddzCard.getAllCards());
		
		if(landlordPosition == 1) {
			{
				Player first = this.seatPlayingMap.get(1).getMaster().getPlayer();
				long coin = landlordWin ? (singleResult * 2) : -(singleResult * 2);
				String reason = landlordWin ? "当地主赢了" : "当地主输了";
				CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
			}
			{
				Player second = this.seatPlayingMap.get(2).getMaster().getPlayer();
				long coin = landlordWin ? (singleResult) : -(singleResult);
				String reason = landlordWin ? "当农民输了" : "当农民赢了";
				CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
			}
			{
				Player third = this.seatPlayingMap.get(3).getMaster().getPlayer();
				long coin = landlordWin ? (singleResult) : -(singleResult);
				String reason = landlordWin ? "当农民输了" : "当农民赢了";
				CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
			}
		}
		else if(landlordPosition == 2) {
			{
				Player second = this.seatPlayingMap.get(2).getMaster().getPlayer();
				long coin = landlordWin ? (singleResult * 2) : -(singleResult * 2);
				String reason = landlordWin ? "当地主赢了" : "当地主输了";
				CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
			}
			{
				Player first = this.seatPlayingMap.get(1).getMaster().getPlayer();
				long coin = landlordWin ? (singleResult) : -(singleResult);
				String reason = landlordWin ? "当农民输了" : "当农民赢了";
				CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
			}
			{
				Player third = this.seatPlayingMap.get(3).getMaster().getPlayer();
				long coin = landlordWin ? (singleResult) : -(singleResult);
				String reason = landlordWin ? "当农民输了" : "当农民赢了";
				CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
			}
		}
		else{
			{
				Player third = this.seatPlayingMap.get(3).getMaster().getPlayer();
				long coin = landlordWin ? (singleResult * 2) : -(singleResult * 2);
				String reason = landlordWin ? "当地主赢了" : "当地主输了";
				CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
			}
			{
				Player first = this.seatPlayingMap.get(1).getMaster().getPlayer();
				long coin = landlordWin ? (singleResult) : -(singleResult);
				String reason = landlordWin ? "当农民输了" : "当农民赢了";
				CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
			}
			{
				Player second = this.seatPlayingMap.get(2).getMaster().getPlayer();
				long coin = landlordWin ? (singleResult) : -(singleResult);
				String reason = landlordWin ? "当农民输了" : "当农民赢了";
				CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
			}
		}
		ResultManager.save(gameResultRecord);
	}
	
	private void handleSurrenderResult() {
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
					Player first = this.seatPlayingMap.get(1).getMaster().getPlayer();
					long coin = (singleResult * 2);
					CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "当地主赢了,对方有投降");
					gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
				}
				{
					Player second = this.seatPlayingMap.get(2).getMaster().getPlayer();
					if(surrenderPosition == 2) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "当农民输了,我投降了");
						gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
					}
				}
				{
					Player third = this.seatPlayingMap.get(3).getMaster().getPlayer();
					if(surrenderPosition == 3) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "当农民输了,我投降了");
						gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
					}
				}
			}
			else if(landlordPosition == 2) {
				{
					Player second = this.seatPlayingMap.get(2).getMaster().getPlayer();
					long coin = (singleResult * 2);
					CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "当地主赢了,对方有投降");
					gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
				}
				{
					Player first = this.seatPlayingMap.get(1).getMaster().getPlayer();
					if(surrenderPosition == 1) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "当农民输了,我投降了");
						gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
					}
				}
				{
					Player third = this.seatPlayingMap.get(3).getMaster().getPlayer();
					if(surrenderPosition == 3) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "当农民输了,我投降了");
						gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
					}
				}
			}
			else{
				{
					Player third = this.seatPlayingMap.get(3).getMaster().getPlayer();
					long coin = (singleResult * 2);
					CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "当地主赢了,对方有投降");
					gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
				}
				{
					Player first = this.seatPlayingMap.get(1).getMaster().getPlayer();
					if(surrenderPosition == 1) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "当农民输了,我投降了");
						gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
					}
				}
				{
					Player second = this.seatPlayingMap.get(2).getMaster().getPlayer();
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
					Player first = this.seatPlayingMap.get(1).getMaster().getPlayer();
					long coin = -(singleResult * 2);
					CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "当地主输了,我投降了");
					gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
				}
				{
					Player second = this.seatPlayingMap.get(2).getMaster().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "当农民赢了,对方有投降");
					gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
				}
				{
					Player third = this.seatPlayingMap.get(3).getMaster().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "当农民赢了,对方有投降");
					gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
				}
			}
			else if(landlordPosition == 2) {
				{
					Player second = this.seatPlayingMap.get(2).getMaster().getPlayer();
					long coin = -(singleResult * 2);
					CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "当地主输了,我投降了");
					gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
				}
				{
					Player first = this.seatPlayingMap.get(1).getMaster().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "当农民赢了,对方有投降");
					gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
				}
				{
					Player third = this.seatPlayingMap.get(3).getMaster().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "当农民赢了,对方有投降");
					gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
				}
			}
			else{
				{
					Player third = this.seatPlayingMap.get(3).getMaster().getPlayer();
					long coin = -(singleResult * 2);
					CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "当地主输了,我投降了");
					gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
				}
				{
					Player first = this.seatPlayingMap.get(1).getMaster().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "当农民赢了,对方有投降");
					gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
				}
				{
					Player second = this.seatPlayingMap.get(2).getMaster().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "当农民赢了,对方有投降");
					gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
				}
			}
		}
		ResultManager.save(gameResultRecord);
	}
	
	private SeatPlayer checkSeatPlayer(long playerId, int position) {
		GameSeat gameSeat = this.seatPlayingMap.get(position);
		if(gameSeat == null) {
			throw new BizException("无效的请求");
		}
		if(gameSeat.getMaster().getPlayer().getId() == playerId) {
			return gameSeat.getMaster(); 
		}
		for(SeatPlayer seatPlayer : gameSeat.getAssistantList()) {
			if(seatPlayer.getPlayer().getId() == playerId) {
				return seatPlayer; 
			}
		}
		throw new BizException("旁观人员不能操作");
	}
	
	
	
	protected DoubleKind getDoubleKind() {
		DdzGameItem config = getDdzGameItem();
		if(config.getDoubleKind() != null ) {
			return config.getDoubleKind();
		}
		return DoubleKind.exponential;
	}
	protected DdzGameItem getDdzGameItem() {
		return (DdzGameItem)this.getCurrentGame().getGameItem();
	}
	protected DdzDeskItem getDdzDeskItem() {
		return (DdzDeskItem)this.getCurrentGame().getDeskItem();
	}
	@Override
	protected GameSeat buildGameSeat(int position){
		return new DdzGameSeat(this, position);
	}
}
