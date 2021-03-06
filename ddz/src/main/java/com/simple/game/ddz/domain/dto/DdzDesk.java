package com.simple.game.ddz.domain.dto;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.rtn.game.RtnGameInfoCmd;
import com.simple.game.core.domain.cmd.rtn.seat.RtnGameSeatInfoCmd;
import com.simple.game.core.domain.cmd.vo.DdzSeatPlayerVo;
import com.simple.game.core.domain.cmd.vo.PlayerVo;
import com.simple.game.core.domain.dto.GameSeat;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.SeatPlayer;
import com.simple.game.core.domain.dto.TableDesk;
import com.simple.game.core.domain.dto.constant.PokerKind;
import com.simple.game.core.domain.dto.constant.SCard;
import com.simple.game.core.domain.dto.constant.SeatPost;
import com.simple.game.core.domain.manager.CoinManager;
import com.simple.game.core.exception.BizException;
import com.simple.game.ddz.domain.cmd.push.game.notify.NotifyDoubledCmd;
import com.simple.game.ddz.domain.cmd.push.game.notify.NotifyGameOverCmd;
import com.simple.game.ddz.domain.cmd.push.game.notify.NotifyGameSkipCmd;
import com.simple.game.ddz.domain.cmd.push.game.notify.NotifySendCardCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushPlayCardCmd;
import com.simple.game.ddz.domain.cmd.rtn.game.RtnDdzGameInfoCmd;
import com.simple.game.ddz.domain.cmd.rtn.seat.RtnDdzGameSeatCmd;
import com.simple.game.ddz.domain.dto.config.DdzDeskItem;
import com.simple.game.ddz.domain.dto.config.DdzGameItem;
import com.simple.game.ddz.domain.dto.constant.ddz.DoubleKind;
import com.simple.game.ddz.domain.dto.constant.ddz.GameProgress;
import com.simple.game.ddz.domain.manager.GameResultRecord;
import com.simple.game.ddz.domain.manager.GameResultRecord.ResultItem;
import com.simple.game.ddz.domain.manager.ResultManager;
import com.simple.game.ddz.domain.ruler.DdzCard;
import com.simple.game.ddz.domain.ruler.DdzCard.PlayCardResult;
import com.simple.game.ddz.domain.ruler.DdzRuler;

import lombok.Getter;
import lombok.ToString;

/***
 * ?????????????????????
 * 
 * @author zhibozhang
 *
 */
@ToString
@Getter
public class DdzDesk extends TableDesk{
	private static Logger logger = LoggerFactory.getLogger(DdzDesk.class);
	
	protected final DdzCard ddzCard = new DdzCard();
	
	/***
	 * ??????????????????
	 * KEY: position
	 * VALUE: doubleCount
	 */
	protected final HashMap<Integer, Integer> doubledMap = new HashMap<Integer, Integer>();
	
	public DdzDesk(DdzGameItem gameItem, DdzDeskItem deskItem) {
		super(gameItem, deskItem);
	} 

	/***??????????????????****/
	protected GameProgress currentProgress = GameProgress.ready;
	private boolean settling = false;
	
	
	/***?????????***/
	protected int surrenderPosition;
	
	/***???????????????????????????***/
	protected long lastGameOverTime;
	/***????????????????????????***/
	protected long lastSendCardTime;
	/***????????????????????????***/
	protected long lastRobLandlordTime;
	
	/***????????????????????????***/
	protected long lastPlayCardTime;
	
	protected boolean onScan() {
		if(currentProgress == GameProgress.ready) {
			if(this.playerMap.size() == 0) {
				return false;
			}
			//?????????
			handleDisconnectPlayer();
			
			//???????????????
			for(int position = deskItem.getMinPosition(); position <= deskItem.getMaxPosition(); position++) {
				DdzGameSeat gameSeat = (DdzGameSeat)this.seatPlayingMap.get(position);
				gameSeat.handleChangeMaster();
			}
			
			boolean standuped = false;
			int readyCount = 0;
			for(int position = deskItem.getMinPosition(); position <= deskItem.getMaxPosition(); position++) {
				DdzGameSeat gameSeat = (DdzGameSeat)this.seatPlayingMap.get(position);
				if(gameSeat == null) {
					logger.error("???????????????bug!!! ???????????????");
					return false;
				}
				
				if(!gameSeat.isReady()) {
					//??????????????????
					if(gameSeat.getFansCount() > 0) {
						standuped = forceStandUpPosition(gameSeat);
					}
					continue;
				}
				
				if(gameSeat.getMaster().get() == null) {
					return false;
				}
				readyCount++;
			}
			if(standuped) {
				return standuped;
			}
			
			if(readyCount < 3) {
				return false;
			}
			
			//???????????????
			//1.?????????
			shuffleCards();
			//2.?????????3.??????
			sendCards();
			currentProgress = GameProgress.sended;	
			return true;
		}
		else if(currentProgress == GameProgress.sended) {
			//?????????????????????????????????
			return handleWaitRobLandlordTimeout();
		}
		else if(currentProgress == GameProgress.robbedLandlord) {
			//??????????????????????????????
			return handleDoubledTimeout();
		}
		else if(currentProgress == GameProgress.doubled) {
			//??????????????????????????????
			return handleWaitPlayCardTimeout();
		}
		else if(currentProgress == GameProgress.gameover || currentProgress == GameProgress.surrender ) {
			//????????????
			this.settle();
		}
		return false;
	}
	
	/***
	 * key position
	 * @return
	 */
	public HashMap<String, ? extends PlayerVo> getSeatMasterPlayer(){
		HashMap<String, DdzSeatPlayerVo> map = new HashMap<String, DdzSeatPlayerVo>(seatPlayingMap.size());
		for(Integer position : seatPlayingMap.keySet()) {
			DdzGameSeat gameSeat = (DdzGameSeat)seatPlayingMap.get(position);
			SeatPlayer master = gameSeat.getMaster().get();
			if(master != null && master.getPlayer() != null) {
				DdzSeatPlayerVo vo = new DdzSeatPlayerVo();
				vo.setId(master.getPlayer().getId());
				vo.setNickname(master.getPlayer().getNickname());
				vo.setGameLevel(master.getPlayer().getGameLevel());
				vo.setExpValue(master.getPlayer().getExpValue());
				vo.setVipLevel(master.getPlayer().getVipLevel());
				vo.setHeadPic(master.getPlayer().getHeadPic());
				vo.setSeatPost(master.getSeatPost());
				vo.setPosition(position);
				
				List<Integer> residueCards = getResidueCard(position);
				if(residueCards == null) {
					vo.setResidueCount(0);
				}
				else {
					vo.setResidueCount(residueCards.size());
				}
				//vo.setResidueCards(null);
				map.put(position + "", vo);
			}
		}
		return map;
	}
	/****??????????????????***/
	public List<Integer> getResidueCard(int position) {
		if(position == 1) {
			return ddzCard.getFirstCardList();
		}
		if(position == 2) {
			return ddzCard.getSecondCardList();
		}
		if(position == 3) {
			return ddzCard.getThirdCardList();
		}
		return null;
	}
	/****????????????????????????????????????***/
	public int getLeftSecond() {
		if(currentProgress == GameProgress.sended) {
			long time = (lastSendCardTime + this.getDdzGameItem().getMaxRobbedLandlordSecond()* 1000) - System.currentTimeMillis();
			if(time <= 0) {
				return 0;
			}
			return (int)(time / 1000);
		}
		else if(currentProgress == GameProgress.robbedLandlord) {
			long time = (lastRobLandlordTime + this.getDdzGameItem().getMaxDoubleSecond()* 1000) - System.currentTimeMillis();
			if(time <= 0) {
				return 0;
			}
			return (int)(time / 1000);
		}
		else if(currentProgress == GameProgress.doubled) {
			long time = (lastPlayCardTime + this.getDdzGameItem().getMaxPlayCardSecond()* 1000) - System.currentTimeMillis();
			if(!this.ddzCard.isStarted()) {
				time = (lastPlayCardTime + this.getDdzGameItem().getMaxFirstPlayCardSecond()* 1000) - System.currentTimeMillis();
			}
			if(time <= 0) {
				return 0;
			}
			return (int)(time / 1000);
		}
		return 0;
	}
	
	public RtnGameInfoCmd getGameInfo() {
		RtnGameInfoCmd gameInfo = super.getGameInfo();
		RtnDdzGameInfoCmd rtnCmd = RtnDdzGameInfoCmd.copy(gameInfo);
		rtnCmd.setCurrentProgress(currentProgress);
		if(currentProgress == GameProgress.ready) {
			return rtnCmd;
		}
		
		if(currentProgress == GameProgress.sended) {
			return rtnCmd;
		}
		
		rtnCmd.setCommonCards(ddzCard.getCommonCardList());
		rtnCmd.setLandlordPosition(ddzCard.getLandlordPosition());
		if(currentProgress == GameProgress.robbedLandlord) {
			return rtnCmd;
		}
		
		int doubleFinal = getDoubleKind().getFinalDouble(ddzCard.getDoubleCount());
		rtnCmd.setDoubleFinal(doubleFinal);
		rtnCmd.setCurrentPosition(ddzCard.getCurrentPosition());
		Object[] spanArray = ddzCard.getBattlefield().getData();
		if(spanArray != null && spanArray.length > 0) {
			List<List<Integer>> battlefield = new ArrayList<List<Integer>>(spanArray.length);
			rtnCmd.setBattlefield(battlefield);
			
			for(Object temp : spanArray) {
				DdzRuler.SpanCard spanCard = (DdzRuler.SpanCard)temp;
				if(temp == null) {
					battlefield.add(null);
					continue;
				}
				
				battlefield.add(PokerKind.convertFaceList(spanCard.getCards()));
			}
		}
		rtnCmd.setLandlordPlayCardCount(ddzCard.getLandlordPlayCardCount());
		rtnCmd.setFarmerPlayCardCount(ddzCard.getFarmerPlayCardCount());
		if(currentProgress == GameProgress.doubled) {
			return rtnCmd;
		}
		
		rtnCmd.setSurrenderPosition(surrenderPosition);
		
		
//		if(currentProgress == GameProgress.gameover) {
//		}
		
		return rtnCmd;
	}
	
	public RtnDdzGameSeatCmd getSeatInfo(RtnGameSeatInfoCmd seatInfo) {
		RtnDdzGameSeatCmd rtnCmd = new RtnDdzGameSeatCmd();
		rtnCmd.copy(seatInfo);
		
		if(seatInfo.getPosition() == 1) {
			rtnCmd.setCards(ddzCard.getFirstCardList());
		}
		if(seatInfo.getPosition() == 2) {
			rtnCmd.setCards(ddzCard.getSecondCardList());
		}
		if(seatInfo.getPosition() == 3) {
			rtnCmd.setCards(ddzCard.getThirdCardList());
		}
		
		DdzGameSeat gameSeat = (DdzGameSeat)this.seatPlayingMap.get(seatInfo.getPosition());
		rtnCmd.setReady(gameSeat.isReady());
		rtnCmd.setSkipCount(gameSeat.getSkipCount());
		rtnCmd.setTimeoutCount(gameSeat.getTimeoutCount());
		return rtnCmd;
	}
	
	/***
	 * ?????????????????????
	 */
	private void handleDisconnectPlayer() {
		List<Player> list = new ArrayList<Player>(offlineMap.values());
		for(Player player : list) {
			long time = System.currentTimeMillis() - player.getOnline().getDisconnectTime();
			if(time == 0 || time / 1000 < this.getDdzGameItem().getMaxDisconnectSecond()) {
				continue;
			}
			
			//????????????????????????????????????
			for(GameSeat gameSeat : seatPlayingMap.values()) {
				SeatPlayer seatPlayer = gameSeat.getSeatPlayer(player.getId());
				if(seatPlayer != null) {
					if(seatPlayer.getSeatPost() != SeatPost.master) {
						gameSeat.standUp(player.getId());
					}
					else {
						gameSeat.standupAll();
					}
				}
			}
			
			//????????????
			this.left(player.getId());
		}
	}
	
	private boolean handleWaitRobLandlordTimeout() {
		long time = System.currentTimeMillis() - lastSendCardTime; 
		if(time == 0 || time / 1000 < this.getDdzGameItem().getMaxRobbedLandlordSecond()) {
			return false;
		}
		
		//??????????????????????????????
		handleNext();
		
		//????????????
		NotifyGameSkipCmd notifyCmd = new NotifyGameSkipCmd();
		broadcast(notifyCmd);
		return true;
	}
	
	/***
	 * ?????????????????????
	 * @return
	 */
	private boolean handleDoubledTimeout() {
		//???????????????
		long time = System.currentTimeMillis() - lastRobLandlordTime; 
		if(time == 0) {
			return false;
		}
		long second = time / 1000; 
		if(second < this.getDdzGameItem().getMaxDoubleSecond()) {
			return false;
		}
		
		finishedDoubled();
		return true;
	}
	
	private void finishedDoubled() {
		currentProgress = GameProgress.doubled;
		lastPlayCardTime = System.currentTimeMillis();
		NotifyDoubledCmd nofifyCmd = new NotifyDoubledCmd();
		this.broadcast(nofifyCmd, true);
	}
	
	/***
	 * ?????????????????????
	 * @return
	 */
	private boolean handleWaitPlayCardTimeout() {
		//???????????????
		long time = System.currentTimeMillis() - lastPlayCardTime; 
		if(time == 0) {
			return false;
		}
		long second = time / 1000; 
		if(second < this.getDdzGameItem().getDisconnectPlayCardSecond()) {
			return false;
		}
		
		int position = this.ddzCard.getCurrentPosition(); 
		//???????????????????????????????????????0
		DdzGameSeat gameSeat = (DdzGameSeat)this.getGameSeat(position);
		if(!gameSeat.isDiconnectPlayCard()) {
			//??????????????????
			if(second < this.getDdzGameItem().getMaxPlayCardSecond()) {
				return false;
			}
			else {
				//????????????????????????
				if(!this.ddzCard.isStarted()) {
					if(second < this.getDdzGameItem().getMaxFirstPlayCardSecond()) {		
						return false;
					}
				}
				gameSeat.timeoutCountIncrease();
			}
		}
		else {
			gameSeat.skipCountIncrease();
		}
		
		//????????????
		List<SCard> outCards = new ArrayList<SCard>(1); 
		PlayCardResult playCardResult = this.ddzCard.autoPlayCard(outCards);
		afterPlayCard(playCardResult.isGameOver());
		
		//??????
		int doubleFinal = getDoubleKind().getFinalDouble(playCardResult.getDoubleCount());
		PushPlayCardCmd pushCmd = new PushPlayCardCmd();
		pushCmd.setForceSend(true);
		pushCmd.setPosition(position);
		pushCmd.setDoubleFinal(doubleFinal);
		pushCmd.setResidueCount(playCardResult.getResidueCount());
		pushCmd.getCards().addAll(PokerKind.convertFaceList(outCards));
		this.broadcast(pushCmd, true);
		return true;
	}
	
	/****
	 * ?????????????????????????????????
	 */
	private boolean forceStandUpPosition(DdzGameSeat gameSeat) {
		long time = System.currentTimeMillis() - lastGameOverTime; 
		if(time != 0 && time / 1000 > this.getDdzGameItem().getMaxReadyNextSecond()) {
			//?????????????????????????????????????????????
			gameSeat.standupAll();
			logger.info("?????????????????????{}?????????{}????????????????????????????????????????????????", time, gameSeat.getPosition());
			return true;
		}
		
		//?????????????????????????????????????????????
		if(!gameSeat.getMaster().get().getPlayer().getOnline().isOnline()) {
			//????????????????????????
			time = System.currentTimeMillis() - gameSeat.getMaster().get().getPlayer().getOnline().getDisconnectTime(); 
			if(time != 0 && time / 1000 > this.getDdzGameItem().getMaxMasterDisconnectSecond()) {
				gameSeat.standupAll();
				logger.info("?????????????????????{}?????????{}???????????????????????????????????????", time, gameSeat.getPosition());
				return true;
			}
		}
		
		//??????????????????????????????????????????
		if(gameSeat.getTimeoutCount() > this.getDdzGameItem().getMaxPlayCardOuttimeCount()) {
			gameSeat.standupAll();
			logger.info("?????????????????????{}?????????{}??????????????????????????????????????????", time, gameSeat.getPosition());
			return true;
		}
		
		//?????????????????????????????????
		if(gameSeat.getSkipCount() > this.getDdzGameItem().getMaxSkipCount()) {
			gameSeat.standupAll();
			logger.info("?????????????????????{}?????????{}?????????????????????????????????????????????", time, gameSeat.getPosition());
			return true;
		}
		return false;
	}
	
	public boolean canStandUp() {
		return currentProgress == GameProgress.ready;
	}
	private void shuffleCards() {
		this.ddzCard.shuffleCards();
	}
	private void sendCards() {
		this.ddzCard.sendCards();
		//??????????????????
		{
			GameSeat gameSeat = this.seatPlayingMap.get(1);
			NotifySendCardCmd pushCmd = new NotifySendCardCmd();
			pushCmd.setPosition(1);
			pushCmd.getCards().addAll(this.ddzCard.getFirstCardList());
			gameSeat.broadcast(pushCmd);
		}
		{
			GameSeat gameSeat = this.seatPlayingMap.get(2);
			NotifySendCardCmd pushCmd = new NotifySendCardCmd();
			pushCmd.setPosition(1);
			pushCmd.getCards().addAll(this.ddzCard.getSecondCardList());
			gameSeat.broadcast(pushCmd);
		}
		{
			GameSeat gameSeat = this.seatPlayingMap.get(3);
			NotifySendCardCmd pushCmd = new NotifySendCardCmd();
			pushCmd.setPosition(1);
			pushCmd.getCards().addAll(this.ddzCard.getThirdCardList());
			gameSeat.broadcast(pushCmd);
		}
		lastSendCardTime = System.currentTimeMillis();
	}
	

	protected Player buildGamePlayer(Player player) {
		return new DdzPlayer(player);
	}
	
	/***
	 * ?????????
	 * @param playerId
	 * @param position
	 * @param score		???????????????????????????
	 */
	public synchronized List<Integer> robLandlord(int position, int score, OutParam<List<Integer>> outParam) {
		if(currentProgress != GameProgress.sended) {
			if(currentProgress != GameProgress.robbedLandlord) {
				throw new BizException("?????????????????????????????????????????????");
			}
			String msg = String.format("????????????%s???????????????", surrenderPosition);
			throw new BizException(msg);
		}
		
		List<Integer> commonCards = this.ddzCard.setLandlord(position, outParam);
		currentProgress = GameProgress.robbedLandlord;
		lastRobLandlordTime = System.currentTimeMillis();
		return commonCards;
	}
	
	/***
	 * ??????
	 * @param playerId
	 * @param doubleCount
	 */
	public synchronized int doubled(int position, OutParam<Boolean> outParam) {
		boolean result = doubled(position, 1);
		int doubleFinal = getDoubleKind().getFinalDouble(ddzCard.getDoubleCount());
		outParam.setParam(result);
		return doubleFinal;
	}
	
	private boolean doubled(int position, int doubleCount) {
		if(currentProgress != GameProgress.robbedLandlord) {
			throw new BizException("?????????????????????????????????????????????");
		}
		//????????????????????????
		if(doubledMap.containsKey(position)) {
			throw new BizException("???????????????????????????");
		}
		
		this.ddzCard.doubleCountIncrAndGet(doubleCount);
		doubledMap.put(position, doubleCount);
		if(doubledMap.size()== 3) {
			currentProgress = GameProgress.doubled;
			lastPlayCardTime = System.currentTimeMillis();
			
			return true;
		}
		return false;
	}
	
	/***
	 * ??????
	 * @param position
	 * @param outParam
	 * @return
	 */
	public synchronized DoubledShowCardResult doubledShowCard(int position) {
		boolean result = doubled(position, 3);
		int doubleFinal = getDoubleKind().getFinalDouble(ddzCard.getDoubleCount());
		DoubledShowCardResult doubledShowCardResult = new DoubledShowCardResult();
		doubledShowCardResult.next = result;
		doubledShowCardResult.doubleFinal = doubleFinal;
		if(position == 1) {
			doubledShowCardResult.cards = ddzCard.getFirstCardList();
		}
		else if(position == 2) {
			doubledShowCardResult.cards = ddzCard.getSecondCardList();
		}
		else if(position == 3) {
			doubledShowCardResult.cards = ddzCard.getThirdCardList();
		}
		return doubledShowCardResult;
	}
	
	@Getter
	public class DoubledShowCardResult{
		private int doubleFinal;
		private List<Integer> cards;
		private boolean next;
	}

	
	public synchronized PlayCardResult playCard(int position, List<Integer> cards) {
		if(currentProgress != GameProgress.doubled) {
			throw new BizException("??????????????????????????????????????????");
		}
		
		PlayCardResult playCardResult = this.ddzCard.playCard(position, cards);
		afterPlayCard(playCardResult.isGameOver());
		return playCardResult;
	}
	
	
	private void afterPlayCard(boolean isGameOver) {
		if(isGameOver) {
			//??????gameover?????????
			currentProgress = GameProgress.gameover;			
		}
		this.lastPlayCardTime = System.currentTimeMillis();
	}
	
	/***
	 * ????????????
	 * ????????????@com.simple.game.ddz.domain.dto.config.DdzGameItem.punishSurrenderDoubleCount??????
	 * @param playerId
	 * @param position
	 */
	public synchronized void surrender(int position) {
		if(currentProgress != GameProgress.doubled) {
			throw new BizException("??????????????????????????????????????????");
		}
		
		//??????surrender?????????
		currentProgress = GameProgress.surrender;			
		surrenderPosition = position;
	}
	

	protected RtnGameInfoCmd getRtnGameInfoCmd() {
		//TODO 
		return new RtnGameInfoCmd();
	}
	
	
	/***
	 * ????????????
	 */
	private boolean settle() {
		if(currentProgress != GameProgress.gameover && currentProgress != GameProgress.surrender) {
			logger.error("?????????????????????bug???????????????????????????");
			throw new BizException("??????????????????");
		}
		if(settling) {
			return false;
		}
		settling = true;
		
		GameResultRecord gameResultRecord = null;
		if(currentProgress == GameProgress.gameover) {
			gameResultRecord = handleNormalResult();
		}
		else if(currentProgress == GameProgress.surrender) {
			gameResultRecord = handleSurrenderResult();
		}
		
		//??????????????????
		handleEscapeResult(gameResultRecord);
		
		//?????????????????????
		handleNext();
		settling = false;
		
		//????????????
		NotifyGameOverCmd notifyCmd = NotifyGameOverCmd.valueOf(gameResultRecord);
		this.broadcast(notifyCmd);
		return true;
	}
	private void handleNext() {
		currentProgress = GameProgress.ready;
		doubledMap.clear();
		ddzCard.readyNext();
		
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
		
		for(int position = deskItem.getMinPosition(); position <= deskItem.getMinPosition(); position++) {
			DdzGameSeat gameSeat = (DdzGameSeat)this.seatPlayingMap.get(position);
			if(gameSeat.getSkipCount() <= this.getDdzGameItem().getEscape2SkipCount()) {
				continue;
			}
			
			//????????????(??????????????????????????????????????????)
			CoinManager.changeCoin(gameSeat.getMaster().get().getPlayer(), -record.getSingleResult()*3, record.getBatchNo(), "???????????????");
			
			String reason = String.format("%s????????????????????????", position);
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
	
	private GameResultRecord handleNormalResult() {
		int doubleCount = this.ddzCard.getDoubleCount();
		if(this.ddzCard.isSpring()) {
			doubleCount += 1;
		}
		int doubleFinal = this.getDoubleKind().getFinalDouble(doubleCount);
		long singleResult = doubleFinal * this.getDdzDeskItem().getUnitPrice();
		int landlordPosition = this.ddzCard.getLandlordPosition();
		boolean landlordWin = this.ddzCard.isLandlordWin();
		
		GameResultRecord gameResultRecord = new GameResultRecord();
		gameResultRecord.setUnitPrice(this.getDdzDeskItem().getUnitPrice());
		gameResultRecord.setDoubleFinal(doubleFinal);
		gameResultRecord.setSingleResult(singleResult);
		gameResultRecord.setDoubleKind(getDoubleKind());
		gameResultRecord.setLandlordPosition(landlordPosition);
		gameResultRecord.getCards().addAll(this.ddzCard.getAllCardList());
		
		if(landlordPosition == 1) {
			{
				Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
				long coin = landlordWin ? (singleResult * 2) : -(singleResult * 2);
				String reason = landlordWin ? "???????????????" : "???????????????";
				CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
			}
			{
				Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
				long coin = landlordWin ? (-singleResult) : (singleResult);
				String reason = landlordWin ? "???????????????" : "???????????????";
				CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
			}
			{
				Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
				long coin = landlordWin ? -(singleResult) : (singleResult);
				String reason = landlordWin ? "???????????????" : "???????????????";
				CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
			}
		}
		else if(landlordPosition == 2) {
			{
				Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
				long coin = landlordWin ? (singleResult * 2) : -(singleResult * 2);
				String reason = landlordWin ? "???????????????" : "???????????????";
				CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
			}
			{
				Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
				long coin = landlordWin ? -(singleResult) : (singleResult);
				String reason = landlordWin ? "???????????????" : "???????????????";
				CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
			}
			{
				Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
				long coin = landlordWin ? -(singleResult) : (singleResult);
				String reason = landlordWin ? "???????????????" : "???????????????";
				CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
			}
		}
		else{
			{
				Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
				long coin = landlordWin ? (singleResult * 2) : -(singleResult * 2);
				String reason = landlordWin ? "???????????????" : "???????????????";
				CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
			}
			{
				Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
				long coin = landlordWin ? -(singleResult) : (singleResult);
				String reason = landlordWin ? "???????????????" : "???????????????";
				CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
			}
			{
				Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
				long coin = landlordWin ? -(singleResult) : (singleResult);
				String reason = landlordWin ? "???????????????" : "???????????????";
				CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), reason);
				gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
			}
		}
		ResultManager.save(gameResultRecord);
		
		return gameResultRecord;
	}
	
	/***
	 * ???????????????????????????????????????????????????
	 * @return
	 */
	private GameResultRecord handleSurrenderResult() {
		int doubleCount = this.ddzCard.getDoubleCount() + this.getDdzGameItem().getPunishSurrenderDoubleCount();
		int doubleFinal = this.getDoubleKind().getFinalDouble(doubleCount);
		long singleResult = doubleFinal * this.getDdzDeskItem().getUnitPrice();
		int landlordPosition = this.ddzCard.getLandlordPosition();
		boolean landlordWin = (landlordPosition != this.surrenderPosition);
		
		GameResultRecord gameResultRecord = new GameResultRecord();
		gameResultRecord.setUnitPrice(this.getDdzDeskItem().getUnitPrice());
		gameResultRecord.setDoubleFinal(doubleFinal);
		gameResultRecord.setDoubleKind(getDoubleKind());
		gameResultRecord.setLandlordPosition(landlordPosition);
		gameResultRecord.getCards().addAll(this.ddzCard.getAllCardList());
		
		if(landlordWin) {
			if(landlordPosition == 1) {
				{
					Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
					long coin = (singleResult * 2);
					CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "???????????????,???????????????");
					gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
				}
				{
					Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
					if(surrenderPosition == 2) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "???????????????,????????????");
						gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
					}
				}
				{
					Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
					if(surrenderPosition == 3) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "???????????????,????????????");
						gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
					}
				}
			}
			else if(landlordPosition == 2) {
				{
					Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
					long coin = (singleResult * 2);
					CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "???????????????,???????????????");
					gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
				}
				{
					Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
					if(surrenderPosition == 1) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "???????????????,????????????");
						gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
					}
				}
				{
					Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
					if(surrenderPosition == 3) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "???????????????,????????????");
						gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
					}
				}
			}
			else{
				{
					Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
					long coin = (singleResult * 2);
					CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "???????????????,???????????????");
					gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
				}
				{
					Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
					if(surrenderPosition == 1) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "???????????????,????????????");
						gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
					}
				}
				{
					Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
					if(surrenderPosition == 2) {
						long coin = -(singleResult * 2);
						CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "???????????????,????????????");
						gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
					}
				}
			}
		}
		
		
		else {
			//???????????????
			if(landlordPosition == 1) {
				{
					Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
					long coin = -(singleResult * 2);
					CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "???????????????,????????????");
					gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
				}
				{
					Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "???????????????,???????????????");
					gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
				}
				{
					Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "???????????????,???????????????");
					gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
				}
			}
			else if(landlordPosition == 2) {
				{
					Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
					long coin = -(singleResult * 2);
					CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "???????????????,????????????");
					gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
				}
				{
					Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "???????????????,???????????????");
					gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
				}
				{
					Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "???????????????,???????????????");
					gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
				}
			}
			else{
				{
					Player third = this.seatPlayingMap.get(3).getMaster().get().getPlayer();
					long coin = -(singleResult * 2);
					CoinManager.changeCoin(third, coin, gameResultRecord.getBatchNo(), "???????????????,????????????");
					gameResultRecord.getMap().put(3, new ResultItem(third.getId(), coin));
				}
				{
					Player first = this.seatPlayingMap.get(1).getMaster().get().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(first, coin, gameResultRecord.getBatchNo(), "???????????????,???????????????");
					gameResultRecord.getMap().put(1, new ResultItem(first.getId(), coin));
				}
				{
					Player second = this.seatPlayingMap.get(2).getMaster().get().getPlayer();
					long coin = singleResult;
					CoinManager.changeCoin(second, coin, gameResultRecord.getBatchNo(), "???????????????,???????????????");
					gameResultRecord.getMap().put(2, new ResultItem(second.getId(), coin));
				}
			}
		}
		ResultManager.save(gameResultRecord);
		return gameResultRecord;
	}
	
	
	public DoubleKind getDoubleKind() {
		DdzGameItem config = getDdzGameItem();
		if(config.getDoubleKind() != null ) {
			return config.getDoubleKind();
		}
		return DoubleKind.exponential;
	}
	protected DdzGameItem getDdzGameItem() {
		return (DdzGameItem)this.gameItem;
	}
	protected DdzDeskItem getDdzDeskItem() {
		return (DdzDeskItem)this.deskItem;
	}
	@Override
	protected GameSeat buildGameSeat(int position){
		return new DdzGameSeat(this, position);
	}
	
}
