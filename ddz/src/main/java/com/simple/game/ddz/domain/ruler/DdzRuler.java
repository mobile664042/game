package com.simple.game.ddz.domain.ruler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.simple.game.core.domain.dto.constant.PokerKind;
import com.simple.game.core.exception.BizException;

import lombok.Getter;

/***
 * 手牌规则
 * @author zhibozhang
 *
 */
public class DdzRuler {
	public static void isValidCard(List<Integer> cards) {
		for(int card : cards) {
			if(!PokerKind.C_54.isValid(card)) {
				throw new BizException(String.format("无效的牌%s", card));
			}
		}
	}
	
	
	/***一次出牌***/
	@Getter
	public static class SpanCard implements Comparable<SpanCard>{
		int position;
		CardType type;
		List<Integer> cards;
		/***主牌***/
		int masterCard;
		
		

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
					if(masterCard == 2) {
						return 1;
					}
					else if(o.masterCard == 2) {
						return -1;
					}
					else if(masterCard == 1) {
						return 1;
					}
					else if(o.masterCard == 1) {
						return -1;
					}
					else{
						return masterCard > o.masterCard ? 1 : -1;
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
			
			//牌型想同时
			if(masterCard == 2) {
				return 1;
			}
			else if(o.masterCard == 2) {
				return -1;
			}
			else if(masterCard == 1) {
				return 1;
			}
			else if(o.masterCard == 1) {
				return -1;
			}
			else{
				return masterCard > o.masterCard ? 1 : -1;
			}
		}
	}
	
	/***
	 * 构建一牌型
	 * @param cards
	 * @return
	 */
	static SpanCard buildSpanCard(int position, List<Integer> originalCards) {
		isValidCard(originalCards);
		List<Integer> list = new ArrayList<Integer>(originalCards);
		Collections.sort(list);
		
		//判断是否有重复的
		for(int i =0; i<list.size()-1; i++) {
			if(list.get(i).intValue() == list.get(i+1).intValue()) {
				throw new BizException("有相同的牌！！！");
			}
		}
		
		SpanCard spanCard = new SpanCard();
		spanCard.cards = list;
		spanCard.position = position;
		if(list.size() == 1) {
			spanCard.type = CardType.single;
			spanCard.masterCard = list.get(0)/10;
			return spanCard;
		}
		if(list.size() == 2) {
			if(list.get(0) == 200 && list.get(1) == 201) {
				spanCard.type = CardType.king_bombs;
				return spanCard;
			}
			if(list.get(0)/10 != list.get(1)/10) {
				throw new BizException("无效的牌型");
			}
			spanCard.type = CardType.twin;
			spanCard.masterCard = list.get(0)/10;
			return spanCard;
		}
		if(list.size() == 3) {
			if(list.get(0)/10 == list.get(1)/10 && list.get(1) == list.get(2)/10) {
				spanCard.type = CardType.three;
				spanCard.masterCard = list.get(0)/10; 
				return spanCard;	
			}
			throw new BizException("无效的牌型");
		}
		if(list.size() == 4) {
			if(list.get(0)/10 == list.get(1)/10 && list.get(1) == list.get(2)/10 && list.get(2) == list.get(3)/10) {
				spanCard.type = CardType.plain_bombs;
				spanCard.masterCard = list.get(0)/10;
				return spanCard;	
			}
			if(list.get(0)/10 == list.get(1)/10 && list.get(1) == list.get(2)/10) {
				spanCard.type = CardType.three_one;
				spanCard.masterCard = list.get(0)/10;
				return spanCard;	
			}
			if(list.get(1)/10 == list.get(2)/10 && list.get(2) == list.get(3)/10) {
				spanCard.type = CardType.three_one;
				spanCard.masterCard = list.get(1)/10;
				return spanCard;	
			}
			throw new BizException("无效的牌型");
		}
		if(list.size() == 5) {
			if(list.get(0)/10 == list.get(1)/10 && list.get(1) == list.get(2)/10 && list.get(2) == list.get(3)/10) {
				spanCard.type = CardType.four_one;
				spanCard.masterCard = list.get(0)/10;
				return spanCard;	
			}
			if(list.get(1)/10 == list.get(2)/10 && list.get(2) == list.get(3)/10 && list.get(3) == list.get(4)/10) {
				spanCard.type = CardType.four_one;
				spanCard.masterCard = list.get(1)/10;
				return spanCard;	
			}
			if(list.get(0)/10 == list.get(1)/10 && list.get(1) == list.get(2)/10) {
				if(list.get(3)/10 == list.get(4)/10) {
					spanCard.type = CardType.three_twin;
					spanCard.masterCard = list.get(1)/10;
					return spanCard;	
				}
				throw new BizException("无效的牌型");
			}
			if(list.get(2)/10 == list.get(3)/10 && list.get(3) == list.get(4)/10) {
				if(list.get(0)/10 == list.get(1)/10) {
					spanCard.type = CardType.three_twin;
					spanCard.masterCard = list.get(2)/10;
					return spanCard;	
				}
				throw new BizException("无效的牌型");
			}
			//判断是否是顺子
			if(isOrderCard(list)) {
				spanCard.type = CardType.order_card;
				spanCard.masterCard = list.get(4)/10;
				return spanCard;
			}
			throw new BizException("无效的牌型");
		}
		if(list.size() == 6) {
			if(list.get(0)/10 == list.get(1)/10 && list.get(1) == list.get(2)/10 && list.get(2) == list.get(3)/10) {
				spanCard.type = CardType.four_two;
				spanCard.masterCard = list.get(0)/10;
				return spanCard;	
			}
			if(list.get(2)/10 == list.get(3)/10 && list.get(3) == list.get(4)/10 && list.get(4) == list.get(5)/10) {
				spanCard.type = CardType.four_two;
				spanCard.masterCard = list.get(2)/10;
				return spanCard;	
			}
			//判断是否是顺子
			if(isOrderCard(list)) {
				spanCard.type = CardType.order_card;
				spanCard.masterCard = list.get(5)/10;
				return spanCard;
			}
			//判断是否是3连队
			if(isOrderTwin(list)) {
				spanCard.type = CardType.order_twin;
				spanCard.masterCard = list.get(5)/10;
				return spanCard;
			}
			throw new BizException("无效的牌型");
		}
		
		//判断是否是顺子
		if(isOrderCard(list)) {
			spanCard.type = CardType.order_card;
			spanCard.masterCard = list.get(list.size()-1)/10;
			return spanCard;
		}
		else if(isOrderTwin(list)) {
			spanCard.type = CardType.order_twin;
			spanCard.masterCard = list.get(list.size()-1)/10;
			return spanCard;
		}else {
			throw new BizException("无效的牌型");
		}
	}
	
	static boolean isOrderCard(List<Integer> order) {
		for(int i=0; i<order.size() -1; i++) {
			if(order.get(i) / 10 != (order.get(i+1) / 10) -1) {
				return false;
			}
		}
		return true;
	}
	
	static boolean isOrderTwin(List<Integer> order) {
		if(order.size() % 2 != 0 || order.size()<4) {
			return false;
		}
		for(int i=0; i<(order.size()/2); i++) {
			if(order.get(2*i) / 10 != order.get(2*i+1) / 10) {
				return false;
			}
		}
		for(int i=0; i<(order.size()/2)-1; i++) {
			if(order.get(2*i) / 10 != (order.get(2*(i+1)) / 10) -1) {
				return false;
			}
		}
		return true;
	}
	
	
	static enum CardType{
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
