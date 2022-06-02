package com.simple.game.ddz.domain.ruler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.dto.constant.PokerKind;
import com.simple.game.core.domain.dto.constant.SCard;
import com.simple.game.core.exception.BizException;
import com.simple.game.ddz.util.SimpleQueue;

import lombok.Getter;

/***
 * 手牌
 * 
 * 所有动作不加锁，
 * 由外部控制并发
 * 
 * @author zhibozhang
 *
 */
@Getter
public class DdzCard {
	private static Logger logger = LoggerFactory.getLogger(DdzCard.class);
	
	/****全部牌***/
	private final List<SCard> allCards = new LinkedList<SCard>();
	/****公共牌(每一轮完成之前，保持不变)***/
	private final List<SCard> commonCards = new ArrayList<SCard>();
	
	private final List<SCard> firstCards = new ArrayList<SCard>();
	private final List<SCard> secondCards = new ArrayList<SCard>();
	private final List<SCard> thirdCards = new ArrayList<SCard>();
	
	/***地主位***/
	private int landlordPosition;
	
	/***当前位位置***/
	private int currentPosition;
	
	/***上一次出的牌(需要对抗的牌)***/
	private SimpleQueue<DdzRuler.SpanCard> battlefield = new SimpleQueue<DdzRuler.SpanCard>(2);
	
	/***总共翻倍几次***/
	private int doubleCount = 1;
	
	
	/***地主出几次牌***/
	private int landlordPlayCardCount;
	
	/***农夫出了几次牌***/
	private int farmerPlayCardCount;
	
	
	
	public List<Integer> setLandlord(int position, OutParam<List<Integer>> outParam) {
		landlordPosition = position;
		currentPosition = position;
		if(landlordPosition == 1) {
			firstCards.addAll(commonCards);
			//自动排序，方便后面自动过最小牌
			Collections.sort(firstCards, new PokerComparator());
			outParam.setParam(PokerKind.convertFaceList(firstCards));
		}
		if(landlordPosition == 2) {
			secondCards.addAll(commonCards);
			//自动排序，方便后面自动过最小牌
			Collections.sort(secondCards, new PokerComparator());
			outParam.setParam(PokerKind.convertFaceList(firstCards));
		}
		if(landlordPosition == 3) {
			thirdCards.addAll(commonCards);
			//自动排序，方便后面自动过最小牌
			Collections.sort(thirdCards, new PokerComparator());
			outParam.setParam(PokerKind.convertFaceList(thirdCards));
		}
		
		return PokerKind.convertFaceList(commonCards);
	}
	
	public void shuffleCards() {
		this.allCards.clear();
		this.allCards.addAll(PokerKind.C_54.shuffleCards());
	}
	
	public void sendCards() {
		for(int i=0; i<17; i++) {
			firstCards.add(this.allCards.remove(0));
			secondCards.add(this.allCards.remove(0));
			thirdCards.add(this.allCards.remove(0));
		}
		commonCards.add(this.allCards.remove(0));
		commonCards.add(this.allCards.remove(0));
		commonCards.add(this.allCards.remove(0));
		
		//自动排序，方便后面自动过最小牌
		Collections.sort(firstCards, new PokerComparator());
		Collections.sort(secondCards, new PokerComparator());
		Collections.sort(thirdCards, new PokerComparator());
		
		logger.info("发牌: common:{}  \n 1:{} \n 2:{} \n 3:{}", commonCards, firstCards, secondCards, thirdCards);
	}

	
	public int getDoubleCount() {
		return doubleCount;
	}
	
	/***
	 * 自动出牌
	 * @param outCards 出过的牌
	 * @return 游戏是否可以结束
	 */
	public PlayCardResult autoPlayCard(List<SCard> outCards) {
		if(!this.battlefield.isNull()) {
			//直接跳过
			playCard(currentPosition, null);
			PlayCardResult playCardResult = new PlayCardResult();
			playCardResult.isGameOver = false;
			playCardResult.doubleCount = doubleCount;
			if(currentPosition == 1) {
				playCardResult.residueCount = this.firstCards.size();
			}
			else if(currentPosition == 2) {
				playCardResult.residueCount = this.secondCards.size();
			}
			else if(currentPosition == 3) {
				playCardResult.residueCount = this.thirdCards.size();
			}
			return playCardResult;
		}
		
		//出一张最小的牌(发完牌后已排序)
		SCard minCard = firstCards.get(0);
		if(currentPosition == 1) {
			minCard = firstCards.get(0);
		}
		else if(currentPosition == 2) {
			minCard = secondCards.get(0);
		}
		else {
			minCard = thirdCards.get(0);
		}
		outCards.add(minCard);
		List<Integer> list = PokerKind.convertFaceList(outCards);
		return playCard(currentPosition, list);
	}
	/***
	 * 正常出牌
	 * @param position
	 * @param tempCards
	 * @param outParam 统计刚才出的牌
	 * @return 返回游戏是否可以结束
	 */
	public PlayCardResult playCard(int position, List<Integer> tempCards) {
		if(position != currentPosition) {
			throw new BizException(String.format("当前只能是%s席位出牌, 不可以由%s出牌", currentPosition, position));
		}
		if(tempCards == null || tempCards.size() == 0) {
			//出空牌
			if(this.battlefield.isNull()) {
				throw new BizException(String.format("轮到你出牌，必须出牌"));	
			}
			
			//出空牌
			this.battlefield.push(null);
			
			if(this.battlefield.isNull()) {
				//如果全部没有人要的话, 直接交给下一个人出牌了
				this.battlefield.clear();	
			}
		}
		else{
			List<SCard> cards = new ArrayList<SCard>(tempCards.size());
			for(int face : tempCards) {
				SCard scard = SCard.valueOf(face);
				cards.add(scard);
			}
			
			//先判断牌面是否正确
			verifyCard(position, cards);
		
			//再判断牌面
			DdzRuler.SpanCard spanCard = DdzRuler.buildSpanCard(position, cards);
			if(this.battlefield.getLast() != null) {
				//需要比较大小
				if(spanCard.compareTo(this.battlefield.getLast()) <= 0) {
					logger.info("当前牌：{}, 上家的牌：{}", spanCard.getCards(), this.battlefield.getLast().getCards());
					
					throw new BizException(String.format("下家的牌必须大于上家的牌，才能出牌"));
				}
			}
			
			if(spanCard.type.doubled) {
				//记录翻倍次数
				doubleCount++;
			}
			
			//计算出牌次数来了计算是否产生『春天』
			if(landlordPosition == position) {
				landlordPlayCardCount++;
			}
			else {
				farmerPlayCardCount++;
			}
				
			//出牌
			this.passCard(cards);
			
			//更换上一个出牌的
			this.battlefield.push(spanCard);
		}
		
		//是否可以结束
		boolean result = canGameOver();
		
		if(!result) {
			//如果还没有结束，需要计算出下一位
			this.nextPosition();
		}
		PlayCardResult playCardResult = new PlayCardResult();
		playCardResult.isGameOver = result;
		playCardResult.doubleCount = doubleCount;
		if(position == 1) {
			playCardResult.residueCount = this.firstCards.size();
		}
		else if(position == 2) {
			playCardResult.residueCount = this.secondCards.size();
		}
		else if(position == 3) {
			playCardResult.residueCount = this.thirdCards.size();
		}
		return playCardResult;
	}
	
	@Getter
	public class PlayCardResult{
		private int doubleCount;
		private int residueCount;
		private boolean isGameOver;
	}
	
	private void verifyCard(int targetPosition, List<SCard> cards) {
		if(cards == null || cards.isEmpty()) {
			return ;
		}
		for(SCard card : cards) {
			if(!PokerKind.C_54.isValid(card)) {
				throw new BizException(String.format("无效的牌%s", card));
			}
		}
		if(targetPosition == 1) {
			for(SCard card : cards) {
				if(!firstCards.contains(card)) {
					logger.warn("firstCards={}, reqCards={}", firstCards, cards);
					throw new BizException(String.format("无中生有的牌%s", card));
				}
			}
		}
		if(targetPosition == 2) {
			for(SCard card : cards) {
				if(!secondCards.contains(card)) {
					logger.warn("secondCards={}, reqCards={}", secondCards, cards);
					throw new BizException(String.format("无中生有的牌%s", card));
				}
			}
		}
		if(targetPosition == 3) {
			for(SCard card : cards) {
				if(!thirdCards.contains(card)) {
					logger.warn("thirdCards={}, reqCards={}", thirdCards, cards);
					throw new BizException(String.format("无中生有的牌%s", card));
				}
			}
		}
	}
	
	/**
	 * 出牌
	 */
	private void passCard(List<SCard> cards) {
		if(currentPosition == 1) {
			firstCards.removeAll(cards);
		}
		if(currentPosition == 2) {
			secondCards.removeAll(cards);
		}
		if(currentPosition == 3) {
			thirdCards.removeAll(cards);
		}
	}
	
	/***计算下一个位置***/
	private void nextPosition() {
		 ++currentPosition;
		 if(currentPosition>3) {
			 currentPosition = 1;
		 }
	}
	public int getCurrentPosition() {
		return currentPosition;
	}
	public int getLandlordPosition() {
		return landlordPosition;
	}
	
	public boolean canGameOver() {
		if(currentPosition == 1) {
			return (firstCards.size() == 0);
		}
		if(currentPosition == 2) {
			return (secondCards.size() == 0) ;
		}
		if(currentPosition == 3) {
			return (thirdCards.size() == 0) ;
		}
		return false;
	}
	
	/***
	 * 是否地主赢了
	 * @return
	 */
	public boolean isLandlordWin() {
		boolean r1 = landlordPosition == 1 && (firstCards.size() == 0);
		boolean r2 = landlordPosition == 2 && (secondCards.size() == 0);
		boolean r3 = landlordPosition == 3 && (thirdCards.size() == 0);
		
		return r1 || r2 || r3;
	}
	
	public boolean isSpring() {
		if(isLandlordWin()) {
			return farmerPlayCardCount == 0;
		}
		return landlordPlayCardCount == 1;
	}

	/****
	 * 清理所有状态
	 */
	public void readyNext() {
		allCards.clear();
		commonCards.clear();
		firstCards.clear();
		secondCards.clear();
		thirdCards.clear();
		landlordPosition = 0;
		currentPosition = 0;
		battlefield.clear();
		doubleCount = 1;
		landlordPlayCardCount = 0;
		farmerPlayCardCount = 0;
	}
	
	public List<Integer> getAllCardList(){
		return PokerKind.convertFaceList(allCards);
	}
	public List<Integer> getCommonCardList(){
		return PokerKind.convertFaceList(commonCards);
	}
	public List<Integer> getFirstCardList(){
		return PokerKind.convertFaceList(firstCards);
	}
	public List<Integer> getSecondCardList(){
		return PokerKind.convertFaceList(secondCards);
	}
	public List<Integer> getThirdCardList(){
		return PokerKind.convertFaceList(thirdCards);
	}
}
