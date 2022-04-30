package com.simple.game.core.domain.ruler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.simple.game.core.domain.cmd.push.ddz.util.SimpleQueue;
import com.simple.game.core.domain.dto.constant.PokerKind;
import com.simple.game.core.exception.BizException;

import lombok.Getter;

/***
 * 手牌
 * @author zhibozhang
 *
 */
@Getter
public class DdzCard {
	/****全部牌***/
	private final List<Integer> allCards = new LinkedList<Integer>();
	/****公共牌(每一轮完成之前，保持不变)***/
	private final List<Integer> commonCards = new ArrayList<Integer>();
	
	private final List<Integer> firstCards = new ArrayList<Integer>();
	private final List<Integer> secondCards = new ArrayList<Integer>();
	private final List<Integer> thirdCards = new ArrayList<Integer>();
	
	/***地主位***/
	private int landlordPosition;
	
	/***当前位位置***/
	private int currentPosition;
	
	/***上一次出的牌(需要对抗的牌)***/
	private SimpleQueue<DdzRuler.SpanCard> battlefield = new SimpleQueue<DdzRuler.SpanCard>(2);
	
	/***总共翻倍几次***/
	private int doubleCount;
	
	
	/***地主出几次牌***/
	private int landlordPlayCardCount;
	
	/***农夫出了几次牌***/
	private int farmerPlayCardCount;
	
	
	
	public void setLandlord(int position) {
		landlordPosition = position;
		currentPosition = position;
		if(landlordPosition == 1) {
			firstCards.addAll(commonCards);
		}
		if(landlordPosition == 2) {
			secondCards.addAll(commonCards);
		}
		if(landlordPosition == 3) {
			thirdCards.addAll(commonCards);
		}
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
	}

	
	public int getDoubleCount() {
		return doubleCount;
	}

	/***
	 * 正常出牌
	 * @param position
	 * @param cards
	 * @param outParam 统计刚才出的牌
	 * @return 返回游戏是否可以结束
	 */
	public boolean playCard(int position, List<Integer> cards) {
		if(position != currentPosition) {
			throw new BizException(String.format("当前只能是%s席位出牌, 不可以由%s出牌", currentPosition, position));
		}
		if(cards == null || cards.size() == 0) {
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
			//先判断牌面是否正确
			verifyCard(position, cards);
		
			//再判断牌面
			DdzRuler.SpanCard spanCard = DdzRuler.buildSpanCard(cards);
			if(this.battlefield.getLast() != null) {
				//需要比较大小
				if(spanCard.compareTo(this.battlefield.getLast()) <= 0) {
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
		return result;
	}
	
	private void verifyCard(int targetPosition, List<Integer> cards) {
		if(cards == null || cards.isEmpty()) {
			return ;
		}
		for(int card : cards) {
			if(!PokerKind.C_54.isValid(card)) {
				throw new BizException(String.format("无效的牌%s", card));
			}
		}
		if(targetPosition == 1) {
			for(int card : cards) {
				if(!firstCards.contains(card)) {
					throw new BizException(String.format("无中生有的牌%s", card));
				}
			}
		}
		if(targetPosition == 2) {
			for(int card : cards) {
				if(!secondCards.contains(card)) {
					throw new BizException(String.format("无中生有的牌%s", card));
				}
			}
		}
		if(targetPosition == 3) {
			for(int card : cards) {
				if(!thirdCards.contains(card)) {
					throw new BizException(String.format("无中生有的牌%s", card));
				}
			}
		}
	}
	
	/**
	 * 出牌
	 */
	private void passCard(List<Integer> cards) {
		if(currentPosition == 1) {
			firstCards.removeAll(cards);
		}
		if(landlordPosition == 2) {
			secondCards.removeAll(cards);
		}
		if(landlordPosition == 3) {
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
	
	
}
