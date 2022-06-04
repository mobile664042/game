package com.simple.game.ddz.domain.ruler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.simple.game.core.domain.dto.constant.PokerKind;
import com.simple.game.core.domain.dto.constant.SCard;

/***
 * 斗地主助手
 * 
 * @author zhibozhang
 *
 */
public class DdzHelper {
	public final static int MAX_COUNT = 30;
	
	/***
	 * 洗副好牌，按斗地主方式
	 * @param count 洗牌次数
	 * @return
	 */
	public static List<SCard> shuffleGoodDdzCards(int count){
		int number = count;
		if(count <= 1) {
			return PokerKind.C_54.shuffleCards();
		}
		
		if(count > MAX_COUNT) {
			number = MAX_COUNT; 
		}
		List<SCardCalcItem> list = new ArrayList<SCardCalcItem>(number); 
		for(int i=0; i<number; i++) {
			List<SCard> allCard = PokerKind.C_54.shuffleCards();
			int score = calcDdzTotalScore(allCard);
			SCardCalcItem item = new SCardCalcItem(score, allCard);
			list.add(item);
		}
		Collections.sort(list);
		
		for(int i=0; i<number; i++) {
			System.out.println(list.get(i).score);
		}
		
		
		return list.get(0).allCard;
	}
	
	private static class SCardCalcItem implements Comparable<SCardCalcItem> {
		int score;
		List<SCard> allCard;
		public SCardCalcItem(int score, List<SCard> allCard) {
			this.score = score;
			this.allCard = allCard;
		}
		@Override
		public int compareTo(SCardCalcItem o) {
			return o.score - score;
		}
	}
	
	/***
	 * 计算整副牌总得分
	 * @param allCards
	 * @return
	 */
	private static int calcDdzTotalScore(List<SCard> allCards) {
		List<SCard> targetList = new ArrayList<SCard>(allCards);
		List<SCard> firstCards = new ArrayList<SCard>();
		List<SCard> secondCards = new ArrayList<SCard>();
		List<SCard> thirdCards = new ArrayList<SCard>();
		for(int i=0; i<17; i++) {
			firstCards.add(targetList.remove(0));
			secondCards.add(targetList.remove(0));
			thirdCards.add(targetList.remove(0));
		}
		
		int firstScore = calcDdzScore(firstCards);
		int secondScore = calcDdzScore(secondCards);
		int thirdScore = calcDdzScore(thirdCards);
		
		return firstScore + secondScore + thirdScore;
	}
	
	/***
	 * 计算斗地主单个选手得分
	 * @param cards
	 * @return
	 */
	private static int calcDdzScore(List<SCard> positionCards) {
		List<SCard> orderList = new ArrayList<SCard>(positionCards);
		Collections.sort(orderList, new PokerComparator());
		
		//计算得分，炸蛋加30分，飞机18分，小顺子10分，3个6分，对子1分
		int score = 0;
		for(int i=0; i<orderList.size()-4; ) {
			SCard s0 = orderList.get(i);
			SCard s1 = orderList.get(i+1);
			if(s0.getValue() == s1.getValue() && s0 == SCard.WEAK_KING) {
				score += 30;
				i+=2;
				continue;
			}
			SCard s2 = orderList.get(i+2);
			SCard s3 = orderList.get(i+3);
			if(s0.getValue() == s1.getValue() && s1.getValue() == s2.getValue() && s2.getValue() == s3.getValue()) {
				score += 30;
				i+=4;
				continue;
			}
			if(s0.getValue() == s1.getValue() && s1.getValue() == s2.getValue()) {
				if(i<orderList.size()-6) {
					//飞机
					SCard s4 = orderList.get(i+4);
					SCard s5 = orderList.get(i+5);
					if(s2.getValue()+1 == s3.getValue() && s3.getValue() == s4.getValue() && s4.getValue() == s5.getValue()) {
						score += 18;
						i+=5;
						continue;
					}
				}
				
				score += 6;
				i+=3;
				continue;
			}
			if(s1.getValue() == s2.getValue() && s2.getValue() == s3.getValue()) {
				if(i<orderList.size()-7) {
					//飞机
					SCard s4 = orderList.get(i+4);
					SCard s5 = orderList.get(i+5);
					SCard s6 = orderList.get(i+6);
					if(s3.getValue()+1 == s4.getValue() && s4.getValue() == s5.getValue() && s5.getValue() == s6.getValue()) {
						score += 18;
						i+=7;
						continue;
					}
				}
				
				score += 6;
				i+=4;
				continue;
			}
			
			if(i<orderList.size()-5) {
				SCard s4 = orderList.get(i+4);
				if(s0.getValue()+1 == s1.getValue() && s1.getValue()+1 == s2.getValue() && s2.getValue()+1 == s3.getValue() && s3.getValue()+1 == s4.getValue()) {
					//小顺子
					score += 10;
					i+=1;
					continue;
				}
			}
			
			if(s0.getValue() == s1.getValue()) {
				score += 1;
			}
			if(s1.getValue() == s2.getValue()) {
				score += 1;
			}
			if(s2.getValue() == s3.getValue()) {
				score += 1;
			}
			i+=2;
			continue;
		}
		return score;
	}	
}
