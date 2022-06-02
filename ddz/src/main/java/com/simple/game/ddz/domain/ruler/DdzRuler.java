package com.simple.game.ddz.domain.ruler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.simple.game.core.domain.dto.constant.SCard;
import com.simple.game.core.exception.BizException;

import lombok.Getter;

/***
 * 手牌规则
 * @author zhibozhang
 *
 */
public class DdzRuler {
//	public static void isValidCard(List<Integer> cards) {
//		for(int card : cards) {
//			if(!PokerKind.C_54.isValid(card)) {
//				throw new BizException(String.format("无效的牌%s", card));
//			}
//		}
//	}
	
	
	/***一次出牌***/
	@Getter
	public static class SpanCard implements Comparable<SpanCard>{
		int position;
		CardType type;
		List<SCard> cards;
		/***主牌值***/
		int masterValue;
		
		

		/***-1表示小于***/
		@Override
		public int compareTo(SpanCard o) {
			if(type == CardType.king_bombs) {
				return 1;
			}
			if(o.type == CardType.king_bombs) {
				return -1;
			}
			if(type == CardType.plain_bombs) {
				if(o.type == CardType.plain_bombs) {
					if(masterValue == 2) {
						return 1;
					}
					else if(o.masterValue == 2) {
						return -1;
					}
					else if(masterValue == 1) {
						return 1;
					}
					else if(o.masterValue == 1) {
						return -1;
					}
					else{
						return masterValue > o.masterValue ? 1 : -1;
					}
				}
				else {
					return 1;
				}
			}

			if(o.type == CardType.plain_bombs) {
				return -1;
			}
			
			if(type != o.type) {
				throw new BizException("这两个牌型不可以比较大小");
			}
			
			if(cards.size() != o.cards.size()) {
				throw new BizException("这两个牌型相同，但数量不相等不可以比较大小");
			}
			
			if(o.type == CardType.single) {
				if(cards.get(0) == SCard.STRONG_KING) {
					return 1;
				}
				if(o.cards.get(0) == SCard.STRONG_KING) {
					return -1;
				}
				
				return cards.get(0).getValue() - o.getCards().get(0).getValue();
			}
			
			//牌型想同时
			if(masterValue == 2) {
				return 1;
			}
			else if(o.masterValue == 2) {
				return -1;
			}
			else if(masterValue == 1) {
				return 1;
			}
			else if(o.masterValue == 1) {
				return -1;
			}
			else{
				return masterValue > o.masterValue ? 1 : -1;
			}
		}
	}
	
	/***
	 * 构建一牌型
	 * @param cards
	 * @return
	 */
	static public SpanCard buildSpanCard(int position, List<SCard> originalCards) {
		List<SCard> list = new ArrayList<SCard>(originalCards);
		Collections.sort(list, new PokerComparator());
		
		//判断是否有重复的
		for(int i =0; i<list.size()-1; i++) {
			if(list.get(i).getFace() == list.get(i+1).getFace()) {
				throw new BizException("有相同的牌！！！");
			}
		}
		
		SpanCard spanCard = new SpanCard();
		spanCard.cards = list;
		spanCard.position = position;
		if(list.size() == 1) {
			spanCard.type = CardType.single;
			spanCard.masterValue = list.get(0).getValue();
			return spanCard;
		}
		if(list.size() == 2) {
			if(list.get(0).getFace() == SCard.WEAK_KING.getFace() && list.get(1).getFace() == SCard.STRONG_KING.getFace()) {
				spanCard.type = CardType.king_bombs;
				return spanCard;
			}
			if(list.get(0).getValue() != list.get(1).getValue()) {
				throw new BizException("无效的牌型");
			}
			spanCard.type = CardType.twin;
			spanCard.masterValue = list.get(0).getValue();
			return spanCard;
		}
		if(list.size() == 3) {
			if(list.get(0).getValue() == list.get(1).getValue() && list.get(1).getValue() == list.get(2).getValue()) {
				spanCard.type = CardType.three;
				spanCard.masterValue = list.get(0).getValue(); 
				return spanCard;	
			}
			throw new BizException("无效的牌型");
		}
		if(list.size() == 4) {
			if(list.get(0).getValue() == list.get(1).getValue() && list.get(1).getValue() == list.get(2).getValue() && list.get(2).getValue() == list.get(3).getValue()) {
				spanCard.type = CardType.plain_bombs;
				spanCard.masterValue = list.get(0).getValue();
				return spanCard;	
			}
			if(list.get(0).getValue() == list.get(1).getValue() && list.get(1).getValue() == list.get(2).getValue()) {
				spanCard.type = CardType.three_one;
				spanCard.masterValue = list.get(0).getValue();
				return spanCard;	
			}
			if(list.get(1).getValue() == list.get(2).getValue() && list.get(2).getValue() == list.get(3).getValue()) {
				spanCard.type = CardType.three_one;
				spanCard.masterValue = list.get(1).getValue();
				return spanCard;	
			}
			throw new BizException("无效的牌型");
		}
		if(list.size() == 5) {
			if(list.get(0).getValue() == list.get(1).getValue() && list.get(1).getValue() == list.get(2).getValue() && list.get(2).getValue() == list.get(3).getValue()) {
				spanCard.type = CardType.four_one;
				spanCard.masterValue = list.get(0).getValue();
				return spanCard;	
			}
			if(list.get(1).getValue() == list.get(2).getValue() && list.get(2).getValue() == list.get(3).getValue() && list.get(3).getValue() == list.get(4).getValue()) {
				spanCard.type = CardType.four_one;
				spanCard.masterValue = list.get(1).getValue();
				return spanCard;	
			}
			if(list.get(0).getValue() == list.get(1).getValue() && list.get(1).getValue() == list.get(2).getValue()) {
				if(list.get(3).getValue() == list.get(4).getValue()) {
					spanCard.type = CardType.three_twin;
					spanCard.masterValue = list.get(1).getValue();
					return spanCard;	
				}
				throw new BizException("无效的牌型");
			}
			if(list.get(2).getValue() == list.get(3).getValue() && list.get(3).getValue() == list.get(4).getValue()) {
				if(list.get(0).getValue() == list.get(1).getValue()) {
					spanCard.type = CardType.three_twin;
					spanCard.masterValue = list.get(2).getValue();
					return spanCard;	
				}
				throw new BizException("无效的牌型");
			}
			//判断是否是顺子
			if(isOrderCard(list)) {
				spanCard.type = CardType.order_card;
				spanCard.masterValue = list.get(list.size()-1).getValue();
				return spanCard;
			}
			throw new BizException("无效的牌型");
		}
		if(list.size() == 6) {
			if(list.get(0).getValue() == list.get(1).getValue() && list.get(1).getValue() == list.get(2).getValue() && list.get(2).getValue() == list.get(3).getValue()) {
				spanCard.type = CardType.four_two;
				spanCard.masterValue = list.get(0).getValue();
				return spanCard;	
			}
			if(list.get(2).getValue() == list.get(3).getValue() && list.get(3).getValue() == list.get(4).getValue() && list.get(4).getValue() == list.get(5).getValue()) {
				spanCard.type = CardType.four_two;
				spanCard.masterValue = list.get(2).getValue();
				return spanCard;	
			}
			//判断是否是顺子
			if(isOrderCard(list)) {
				spanCard.type = CardType.order_card;
				spanCard.masterValue = list.get(5).getValue();
				return spanCard;
			}
			//判断是否是3连队
			if(isOrderTwin(list)) {
				spanCard.type = CardType.order_twin;
				spanCard.masterValue = list.get(5).getValue();
				return spanCard;
			}
			//判断是否是飞机
			AtomicInteger masterCard = new AtomicInteger();
			if(isPlane(list, masterCard)) {
				spanCard.type = CardType.plane;
				spanCard.masterValue = masterCard.get();
				return spanCard;
			}
			
			throw new BizException("无效的牌型");
		}
		if(list.size() == 7) {
			if(isOrderCard(list)) {
				spanCard.type = CardType.order_twin;
				spanCard.masterValue = list.get(list.size()-1).getValue();
				return spanCard;
			}
			throw new BizException("无效的牌型");
		}
		
		
		AtomicInteger masterCard = new AtomicInteger();
		if(isOrderCard(list)) {
			//判断是否是顺子
			spanCard.type = CardType.order_card;
			spanCard.masterValue = list.get(list.size()-1).getValue();
			return spanCard;
		}
		else if(isPlane(list, masterCard)) {
			//判断是否是飞机
			spanCard.type = CardType.plane;
			spanCard.masterValue = masterCard.get();
			return spanCard;
		}
		else if(isOrderTwin(list)) {
			//判断是否是连对
			spanCard.type = CardType.order_twin;
			spanCard.masterValue = list.get(list.size()-1).getValue();
			return spanCard;
		}else {
			throw new BizException("无效的牌型");
		}
	}
	
	static boolean isOrderCard(List<SCard> order) {
		if(order.contains(SCard.WEAK_KING)) {
			return false;
		}
		if(order.contains(SCard.STRONG_KING)) {
			return false;
		}
		
		//判断是否包含2
		if(order.contains(SCard.c20) || order.contains(SCard.c21) || order.contains(SCard.c22) || order.contains(SCard.c22)) {
			Collections.sort(order, new Poker2Comparator());
			for(int i=0; i<order.size() -1; i++) {
				if(order.get(i).getSv() != (order.get(i+1).getSv()) -1) {
					return false;
				}
			}
			return true;
		}
		
		for(int i=0; i<order.size() -1; i++) {
			if(order.get(i).getValue() != (order.get(i+1).getValue()) -1) {
				return false;
			}
		}
		return true;
	}
	
	static boolean isOrderTwin(List<SCard> order) {
		if(order.size() % 2 != 0 || order.size()<4) {
			return false;
		}
		for(int i=0; i<(order.size()/2)-1; i++) {
			if(order.get(2*i).getValue() != order.get(2*i+1).getValue()) {
				return false;
			}
			if(order.get(2*i).getValue() != (order.get(2*(i+1)).getValue()) -1) {
				return false;
			}
		}
		return true;
	}
	
	static boolean isPlane(List<SCard> order, AtomicInteger masterCard) {
		int size = order.size();
		if(size < 6) {
			return false;
		}
		List<SCard> cards = new ArrayList<SCard>(order);
//		for(SCard temp : order) {
//			cards.add(temp /10);
//		}
		
		if(size != 6 && size != 8 && size != 9 && size != 10 && size != 12 && size != 15 && size == 16 && size == 20) {
			return false;
		}
		
		if(size == 6) {
			if(cards.get(0) != cards.get(1) || cards.get(1) != cards.get(2)) {
				return false;
			}
			if(cards.get(3) != cards.get(4) || cards.get(4) != cards.get(5)) {
				return false;
			}
			masterCard.set(cards.get(3).getValue());
			return cards.get(0).getValue() + 1 == cards.get(3).getValue();
		}
		else if(size == 8) {
			int firstThree = 0;
			int secondThree = 0;
			
			for(int i =0; i<cards.size()-2; i++) {
				Integer f = cards.get(i).getValue();
				Integer s = cards.get(i+1).getValue();
				Integer t = cards.get(i+2).getValue();
				
				if(f == s && s == t) {
					if(firstThree == 0) {
						firstThree = f;
					}
					else {
						if(secondThree == 0) {
							secondThree = f;
							break;
						}
					}
				}
			}
			
			if(firstThree == 0 || secondThree == 0) {
				return false;
			}
			
			masterCard.set(secondThree);
			return firstThree + 1 == secondThree;
		}
		else if(size == 9) {
			if(cards.get(0) != cards.get(1) || cards.get(1) != cards.get(2)) {
				return false;
			}
			if(cards.get(3) != cards.get(4) || cards.get(4) != cards.get(5)) {
				return false;
			}
			if(cards.get(6) != cards.get(7) || cards.get(7) != cards.get(8)) {
				return false;
			}
			
			//如果第一张牌是A
			masterCard.set(cards.get(6).getValue());
			return (cards.get(0).getValue() + 1 == cards.get(3).getValue()) && (cards.get(3).getValue() + 1 == cards.get(6).getValue());
		}
		else if(size == 10) {
			SCard firstThree = null;
			SCard secondThree = null;
			
			for(int i =0; i<cards.size()-2; i++) {
				SCard f = cards.get(i);
				SCard s = cards.get(i+1);
				SCard t = cards.get(i+2);
				
				if(f == s && s == t) {
					if(firstThree == null) {
						firstThree = f;
					}
					else {
						if(secondThree == null) {
							secondThree = f;
							break;
						}
					}
				}
			}
			
			if(firstThree == null || secondThree == null) {
				return false;
			}
			
			cards.remove(firstThree);
			cards.remove(firstThree);
			cards.remove(firstThree);
			cards.remove(secondThree);
			cards.remove(secondThree);
			cards.remove(secondThree);
			
			if(cards.get(0) != cards.get(1)) {
				return false;
			}
			if(cards.get(2) != cards.get(3)) {
				return false;
			}
			
			//如果第一张牌是A
			masterCard.set(secondThree.getValue());
			return firstThree.getValue() + 1 == secondThree.getValue();
		}
		else if(size == 12) {
			Integer firstThree = 0;
			Integer secondThree = 0;
			Integer thirdThree = 0;
			
			for(int i =0; i<cards.size()-2; i++) {
				Integer f = cards.get(i).getValue();
				Integer s = cards.get(i+1).getValue();
				Integer t = cards.get(i+2).getValue();
				
				if(f == s && s == t) {
					if(firstThree == 0) {
						firstThree = f;
					}
					else {
						if(secondThree == 0) {
							secondThree = f;
						}
						else {
							thirdThree = f;
						}
					}
				}
			}
			
			if(firstThree == 0 || secondThree == 0 || thirdThree == 0) {
				return false;
			}
			
			masterCard.set(thirdThree);
			return firstThree + 1 == secondThree && secondThree + 1 == thirdThree;
		}
		else if(size == 15) {
			SCard firstThree = null;
			SCard secondThree = null;
			SCard thirdThree = null;
			
			for(int i =0; i<cards.size()-2; i++) {
				SCard f = cards.get(i);
				SCard s = cards.get(i+1);
				SCard t = cards.get(i+2);
				
				if(f == s && s == t) {
					if(firstThree == null) {
						firstThree = f;
					}
					else {
						if(secondThree == null) {
							secondThree = f;
						}
						else {
							thirdThree = f;
						}
					}
				}
			}
			
			if(firstThree == null || secondThree == null || thirdThree == null) {
				return false;
			}
			
			cards.remove(firstThree);
			cards.remove(firstThree);
			cards.remove(firstThree);
			cards.remove(secondThree);
			cards.remove(secondThree);
			cards.remove(secondThree);
			cards.remove(thirdThree);
			cards.remove(thirdThree);
			cards.remove(thirdThree);
			
			if(cards.get(0) != cards.get(1)) {
				return false;
			}
			if(cards.get(2) != cards.get(3)) {
				return false;
			}
			if(cards.get(4) != cards.get(5)) {
				return false;
			}
			
			masterCard.set(thirdThree.getValue());
			return firstThree.getValue() + 1 == secondThree.getValue() && secondThree.getValue() + 1 == thirdThree.getValue();
		}
		else if(size == 16) {
			Integer firstThree = 0;
			Integer secondThree = 0;
			Integer thirdThree = 0;
			Integer fourthThree = 0;
			
			for(int i =0; i<cards.size()-2; i++) {
				Integer f = cards.get(i).getValue();
				Integer s = cards.get(i+1).getValue();
				Integer t = cards.get(i+2).getValue();
				
				if(f == s && s == t) {
					if(firstThree == 0) {
						firstThree = f;
					}
					else {
						if(secondThree == 0) {
							secondThree = f;
						}
						else {
							if(thirdThree == 0) {
								thirdThree = f;
							}
							else {
								fourthThree = 4;
							}
						}
					}
				}
			}
			
			if(firstThree == 0 || secondThree == 0 || thirdThree == 0 || fourthThree == 0) {
				return false;
			}
						
			return firstThree + 1 == secondThree && secondThree + 1 == thirdThree && thirdThree + 1 == fourthThree;
		}
		else if(size == 20) {
			SCard firstThree = null;
			SCard secondThree = null;
			SCard thirdThree = null;
			SCard fourthThree = null;
			
			for(int i =0; i<cards.size()-2; i++) {
				SCard f = cards.get(i);
				SCard s = cards.get(i+1);
				SCard t = cards.get(i+2);
				
				if(f == s && s == t) {
					if(firstThree == null) {
						firstThree = f;
					}
					else {
						if(secondThree == null) {
							secondThree = f;
						}
						else {
							if(thirdThree == null) {
								thirdThree = f;
							}
							else {
								fourthThree = f;
							}
						}
					}
				}
			}
			
			if(firstThree == null || secondThree == null || thirdThree == null || fourthThree == null) {
				return false;
			}
			
			cards.remove(firstThree);
			cards.remove(firstThree);
			cards.remove(firstThree);
			cards.remove(secondThree);
			cards.remove(secondThree);
			cards.remove(secondThree);
			cards.remove(thirdThree);
			cards.remove(thirdThree);
			cards.remove(thirdThree);
			cards.remove(fourthThree);
			cards.remove(fourthThree);
			cards.remove(fourthThree);
						
			masterCard.set(fourthThree.getValue());
			return firstThree.getValue() + 1 == secondThree.getValue() && secondThree.getValue() + 1 == thirdThree.getValue() && thirdThree.getValue() + 1 == fourthThree.getValue();
		}
		return false;
	}
	
	
	public static enum CardType{
		/***王炸***/
		king_bombs(true),
		
		/***普通炸蛋***/
		plain_bombs(true),
		
		/***顺子***/
		order_card(false),
		
		/***四带一***/
		four_one(false),
		
		/***四带二***/
		four_two(false),
		
		/***三带一***/
		three_one(false),
		
		/***三带一对***/
		three_twin(false),
		
		/***三个***/
		three(false),
		
		/***飞机***/
		plane(false),
		
		/***连对***/
		order_twin(false),
		
		/***一对***/
		twin(false),
		
		/***单个***/
		single(false);
		
		boolean doubled;
		private CardType(boolean doubled) {
			this.doubled = doubled;
		}
		
		/***是否可以翻倍****/
		public boolean isDoubled() {
			return doubled;
		}
	}
	
}
