package com.simple.game.ddz.robot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.simple.game.core.domain.dto.constant.PokerKind;
import com.simple.game.core.domain.dto.constant.SCard;
import com.simple.game.ddz.domain.ruler.DdzRuler;
import com.simple.game.ddz.domain.ruler.DdzRuler.CardType;
import com.simple.game.ddz.domain.ruler.PokerComparator;
import com.simple.game.ddz.util.SimpleQueue;

/***
 * 机器人打牌
 * 
 * @author Administrator
 *
 */
public class RobotPlayer {
	public final static String ROBOT_CACHE = "ROBOT_CACHE";
	
	/***
	 * 拥有的手牌
	 */
	private List<SCard> cards;
	private List<Integer> willRemoveCards;
	private int position;
	/***地主位***/
	private int landlordPosition;
	/***
	 * 其他人出过的牌
	 * key   position
	 * value residueCount 剩余牌数
	 */
	private final HashMap<Integer, Integer> outCards = new HashMap<Integer, Integer>();
	
	/***上一次出的牌(需要对抗的牌)***/
	private SimpleQueue<DdzRuler.SpanCard> battlefield = new SimpleQueue<DdzRuler.SpanCard>(2);
	
	public RobotPlayer() {}

	
	public void setLandlordPosition(int landlordPosition) {
		this.landlordPosition = landlordPosition;
	}
	public void beLandlord() {
		this.landlordPosition = position;
	}


	public void setPosition(int position) {
		this.position = position;
	}
	public void setCards(List<Integer> tempCards) {
		List<SCard> cards = new ArrayList<SCard>(tempCards.size());
		for(int face : tempCards) {
			SCard scard = SCard.valueOf(face);
			cards.add(scard);
		}
		Collections.sort(cards, new PokerComparator());
		this.cards = cards;
	}
	public void addCommonCards(List<Integer> tempCards) {
		for(int face : tempCards) {
			SCard scard = SCard.valueOf(face);
			cards.add(scard);
		}
		Collections.sort(cards, new PokerComparator());
	}
	public void removeCards(int residueCount) {
		if(willRemoveCards != null && willRemoveCards.size() > 0) {
			for(int face : willRemoveCards) {
				SCard scard = SCard.valueOf(face);
				cards.remove(scard);
			}
		}
		
		outCards.put(position, residueCount);
		playCard(position, willRemoveCards);
	}
	public void addOutCards(int position, List<Integer> tempCards, int residueCount) {
		//被强制出牌
		if(position == this.position) {
			if(tempCards == null || tempCards.size() == 0) {
				return;
			}
			if(willRemoveCards != null && willRemoveCards.size() > 0) {
				willRemoveCards.removeAll(tempCards);
			}
			for(int face : tempCards) {
				SCard scard = SCard.valueOf(face);
				cards.remove(scard);
			}
		}
		outCards.put(position, residueCount);
		playCard(position, tempCards);
	}
	
	/***
	 * 正常出牌
	 * @param position
	 * @param cards
	 */
	private void playCard(int position, List<Integer> tempCards) {
		if(tempCards == null || tempCards.size() == 0) {
			this.battlefield.push(null);
			if(this.battlefield.isNull()) {
				this.battlefield.clear();	
			}
		}
		else{
			List<SCard> list = new ArrayList<SCard>(tempCards.size());
			for(int face : tempCards) {
				SCard scard = SCard.valueOf(face);
				list.add(scard);
			}
			DdzRuler.SpanCard spanCard = DdzRuler.buildSpanCard(position, list);
			this.battlefield.push(spanCard);
		}
	}
	
	/***
	 * TODO 傻瓜式出牌
	 * 
	 * @param first
	 * @return
	 */
	public List<Integer> sendCard(boolean first){
		List<SCard> tempCards = new ArrayList<SCard>();
		if(first || this.battlefield.isNull()) {
			//如果只有一张牌
			if(this.cards.size() == 1) {
				tempCards.add(this.cards.get(0));
				return PokerKind.convertFaceList(tempCards);
			}
			
			//按顺序出牌
			tempCards = getFromCardType(0);
			int value = cards.get(0).getValue();
			
			//如果是单牌
			if(tempCards.size() == 1) {
				//判断能否取出顺子
				List<SCard> newCards = new ArrayList<SCard>(tempCards);
				for(int i=1; i<cards.size(); i++) {
					if(value + i == cards.get(i).getValue()) {
						newCards.add(cards.get(i));
					}
					else {
						break;
					}
				}
				if(newCards.size() >= 5) {
					tempCards = newCards;
				}
				else {
					//能否取到3带1
					if(cards.size()==4) {
						if(cards.get(1).getValue() == cards.get(2).getValue() && cards.get(2).getValue() == cards.get(3).getValue()) {
							tempCards.add(cards.get(1));
							tempCards.add(cards.get(2));
							tempCards.add(cards.get(3));
						}
					}
					else if(cards.size()>4) {
						if(cards.get(1).getValue() == cards.get(2).getValue() && cards.get(2).getValue() == cards.get(3).getValue()) {
							if(cards.get(3).getValue() != cards.get(4).getValue()) {
								tempCards.add(cards.get(1));
								tempCards.add(cards.get(2));
								tempCards.add(cards.get(3));	
							}
						}
					}
					
					if(tempCards.size() == 1) {
						boolean enemySingleCard = false;
						if(position == 1) {
							Integer enemyResidueCount2 = outCards.get(2);
							Integer enemyResidueCount3 = outCards.get(3);
							enemySingleCard = (enemyResidueCount2 != null && enemyResidueCount2 == 1) || (enemyResidueCount3 != null && enemyResidueCount3 == 1);
						}
						else if(position == 2) {
							Integer enemyResidueCount1 = outCards.get(1);
							Integer enemyResidueCount3 = outCards.get(3);
							enemySingleCard = (enemyResidueCount1 != null && enemyResidueCount1 == 1) || (enemyResidueCount3 != null && enemyResidueCount3 == 1);
						}
						else if(position == 3) {
							Integer enemyResidueCount1 = outCards.get(1);
							Integer enemyResidueCount2 = outCards.get(2);
							enemySingleCard = (enemyResidueCount1 != null && enemyResidueCount1 == 1) || (enemyResidueCount2 != null && enemyResidueCount2 == 1);
						}
						
						//如果敌方报单
						if(enemySingleCard) {
							//我方不出最小单牌
							tempCards = getFromCardType(1);
						}
					}
				}
			}
			
			//如果是2个，看看是否能够取出三连对
			if(tempCards.size() == 2 && cards.size()>=5) {
				List<SCard> secondCards = new ArrayList<SCard>();
				for(int i=2; i<cards.size(); ) {
					SCard card0 = cards.get(i);
					SCard card1 = cards.get(i+1);
					
					if(card0.getValue() != card1.getValue()) {
						break;
					}
					
					if(card0.getValue() != tempCards.get(0).getValue() + (i/2)) {
						break;
					}
					
					i += 2;
					secondCards.add(card0);
					secondCards.add(card1);
				}
				if(secondCards.size() >= 4) {
					tempCards.addAll(secondCards);
				}
			}
			
			//如果是3个，看看是否能够取出3带1
			if(tempCards.size() == 3 && cards.size()>=4) {
				if(cards.size()==4) {
					tempCards.add(cards.get(3));
				}
				else {
					//找出一个单
					for(int i=3; i<cards.size()-1; i++) {
						SCard card0 = cards.get(i);
						SCard card1 = cards.get(i+1);
						
						if(card0.getValue() == card1.getValue()) {
							i++;
							continue;
						}
						else {
							tempCards.add(cards.get(i));
							break;
						}
					}
				}
			}
			
			//如果是4个
			if(tempCards.size() == 4) {
				if(cards.size()>4) {
					List<SCard> secondCards = getFromCardType(4);
					if(secondCards.size() != 4) {
						tempCards.clear();
						tempCards.addAll(secondCards);
					}
				}
			}
			
			willRemoveCards = PokerKind.convertFaceList(tempCards);
			return PokerKind.convertFaceList(tempCards);
		}
		
		
		
		
		//判断牌型
		DdzRuler.SpanCard spanCard = this.battlefield.getLast();
		tempCards = getBiggerCards(spanCard);
		if(tempCards == null) {
			willRemoveCards = null;
			return willRemoveCards;
		}
		
		willRemoveCards = PokerKind.convertFaceList(tempCards);
		return PokerKind.convertFaceList(tempCards);
	}
	
	/****找出下一次要出的牌***/
	private List<SCard> getFromCardType(int from){
		if(from >= this.cards.size()) {
			return null;
		}
		
		List<SCard> tempCards = new ArrayList<SCard>();
		tempCards.add(cards.get(from));
		int value = cards.get(from).getValue();
		for(int i=from+1; i<cards.size(); i++) {
			if(value == cards.get(i).getValue()) {
				tempCards.add(cards.get(i));
				if(tempCards.size() == 4) {
					if(cards.size() == 4) {
						break;
					}
					else {
						tempCards.clear();
						value = cards.get(i+1).getValue();
						tempCards.add(cards.get(i+1));				
						i++;
						continue;
					}
				}
			}
			else {
				break;
			}
		}
		return tempCards;
	}
	
	/***
	 * 找出更大的牌
	 * @param spanCard
	 * @return
	 */
	private List<SCard> getBiggerCards(DdzRuler.SpanCard spanCard){
		List<SCard> tempCards = new ArrayList<SCard>();
		int v0 = spanCard.getCards().get(0).getValue();
		
		//判断还剩下1张牌,得考虑出炸
		if(spanCard.getPosition() == this.landlordPosition || position == this.landlordPosition) {
			Integer enemyResidueCount = outCards.get(spanCard.getPosition());
			if(enemyResidueCount != null && enemyResidueCount == 1) {
				Integer residueCount = outCards.get(position);
				if((residueCount != null && residueCount<6)) {
					if(spanCard.getType() == CardType.king_bombs) {
						return null;
					}
					else if(spanCard.getType() == CardType.plain_bombs) {
						return getBombsCards(spanCard.getCards().get(0));
					}
					return getBombsCards(null);
				}
			}
		}
		
		
		//判断牌型
		if(spanCard.getType() == CardType.single) {
			//先判断大王
			if(spanCard.getCards().get(0) == SCard.STRONG_KING) {
				//判断是否是
				return adaptBombsCards(spanCard);
			}
			
			if(spanCard.getCards().get(0) == SCard.WEAK_KING) {
				if(cards.contains(SCard.STRONG_KING)) {
					tempCards.add(SCard.STRONG_KING);
					return tempCards;
				}
			}
			
			if(spanCard.getCards().get(0).getValue() == SCard.c20.getValue()) {
				if(cards.contains(SCard.STRONG_KING)) {
					tempCards.add(SCard.STRONG_KING);
					return tempCards;
				}
			}
			
			//如果是个2
			
			//再找一张比它大的牌
			for(int i=0; i<cards.size()-1; i++) {
				int v1 = cards.get(i).getValue();
				int v2 = cards.get(i+1).getValue();
				if(v1 == v2) {
					i++;
					continue;
				}
				
				if(v1 > v0 && v1 != v2) {
					tempCards.add(cards.get(i));
					return tempCards;
				}
			}
			return adaptBombsCards(spanCard);
		}
		
		
		else if(spanCard.getType() == CardType.twin) {
			//如果只有1张牌
			if(cards.size() == 1) {
				return null;
			}
			
			//再判断是否是对2
			if(v0 == SCard.c20.getValue()) {
				return adaptBombsCards(spanCard);
			}
			
			//如果只有两张牌
			if(cards.size() == 2) {
				int v1 = cards.get(0).getValue();
				int v2 = cards.get(1).getValue();
				if(v1 != v2) {
					return null;
				}
				
				if(v1 > v0) {
					return cards;
				}
				return null;
			}
			
			//如果是多张牌
			for(int i=0; i<cards.size()-2; i++) {
				int v1 = cards.get(i).getValue();
				int v2 = cards.get(i+1).getValue();
				int v3 = cards.get(i+2).getValue();
				if(v1 != v2) {
					continue;
				}
				if(v1 <= v0) {
					continue;
				}
				
				if(v2 == v3) {
					i++;
					i++;
					continue;
				}
				
				//找到了目标牌
				tempCards.add(cards.get(i));
				tempCards.add(cards.get(i+1));
				return tempCards;
			}
			return adaptBombsCards(spanCard);
		}
		else if(spanCard.getType() == CardType.three) {
			//如果只有1张牌
			if(cards.size() == 1) {
				return null;
			}
			
			//再判断是否是3个2
			if(v0 == SCard.c20.getValue()) {
				return adaptBombsCards(spanCard);
			}
			
			
			//如果只有两张牌
			if(cards.size() == 2) {
				return adaptBombsCards(spanCard);
			}
			
			
			//如果只有三张牌
			if(cards.size() == 3) {
				int v1 = cards.get(0).getValue();
				int v2 = cards.get(1).getValue();
				int v3 = cards.get(2).getValue();
				if(v1 != v2) {
					return adaptBombsCards(spanCard);
				}
				if(v2 != v3) {
					return adaptBombsCards(spanCard);
				}
				if(v1 > v0) {
					return cards;
				}
				return adaptBombsCards(spanCard);
			}
			
			//如果是多张牌
			for(int i=0; i<cards.size()-3; i++) {
				int v1 = cards.get(i).getValue();
				int v2 = cards.get(i+1).getValue();
				int v3 = cards.get(i+2).getValue();
				int v4 = cards.get(i+3).getValue();
				if(v1 != v2) {
					continue;
				}
				if(v2 != v3) {
					continue;
				}
				if(v1 <= v0) {
					continue;
				}
				if(v3 == v4) {
					i++;
					i++;
					i++;
					continue;
				}
				
				//找到了目标牌
				tempCards.add(cards.get(i));
				tempCards.add(cards.get(i+1));
				tempCards.add(cards.get(i+2));
				return tempCards;
			}
			return adaptBombsCards(spanCard);
		}
		else if(spanCard.getType() == CardType.three_one) {
			//如果只有1张牌
			if(cards.size() == 1) {
				return null;
			}
			v0 = spanCard.getMasterValue();
			
			//再判断是否是3个2
			if(v0 == SCard.c20.getValue()) {
				return adaptBombsCards(spanCard);
			}
			
			//如果只有两或三张牌
			if(cards.size() == 2 || cards.size() == 3) {
				return adaptBombsCards(spanCard);
			}
			
			//如果只有四张牌
			if(cards.size() == 4) {
				int v1 = cards.get(0).getValue();
				int v2 = cards.get(1).getValue();
				int v3 = cards.get(2).getValue();
				int v4 = cards.get(3).getValue();
				if(v2 != v3) {
					return adaptBombsCards(spanCard);
				}
				
				if(v1 == v2) {
					if(v2 > v0) {
						return cards;
					}
					return adaptBombsCards(spanCard); 
				}
				if(v3 > v4) {
					if(v2 > v0) {
						return cards;
					}
					return adaptBombsCards(spanCard); 
				}
				return adaptBombsCards(spanCard);
			}
			
			//如果是多张牌
			for(int i=0; i<cards.size()-3; i++) {
				int v1 = cards.get(i).getValue();
				int v2 = cards.get(i+1).getValue();
				int v3 = cards.get(i+2).getValue();
				int v4 = cards.get(i+3).getValue();
				if(v1 != v2) {
					continue;
				}
				if(v2 != v3) {
					continue;
				}
				if(v1 <= v0) {
					continue;
				}
				if(v3 == v4) {
					i++;
					i++;
					i++;
					continue;
				}
				
				//找到了目标牌
				tempCards.add(cards.get(i));
				tempCards.add(cards.get(i+1));
				tempCards.add(cards.get(i+2));
				//再找一张单牌
				SCard single = null;
				SCard singleBackup = null;
				for(int j=0; j<cards.size()-1; j++) {
					int vv1 = cards.get(j).getValue();
					int vv2 = cards.get(j+1).getValue();
					if(j == i || j == i+1 || j == i+2 ) {
						continue;
					}
					if(vv1 == vv2) {
						if(singleBackup == null) {
							single = cards.get(j);
						}
						j++;
						continue;
					}
					//找到目标单牌
					single = cards.get(j);
					break;
				}
				//如果目标单牌没有找到,找最小的
				if(single == null) {
					single = singleBackup;
				}
				tempCards.add(single);
				return tempCards;
			}
			return adaptBombsCards(spanCard);
		}
		else if(spanCard.getType() == CardType.three_twin) {
			//如果只有1张牌
			if(cards.size() == 1) {
				return null;
			}
			v0 = spanCard.getMasterValue();
			
			//再判断是否是3个2
			if(v0 == SCard.c20.getValue()) {
				return adaptBombsCards(spanCard);
			}
			
			//如果只有两或三张牌
			if(cards.size() == 2 || cards.size() == 3 || cards.size() == 4) {
				return adaptBombsCards(spanCard);
			}
			
			//如果只有五张牌
			if(cards.size() == 5) {
				int v1 = cards.get(0).getValue();
				int v2 = cards.get(1).getValue();
				int v3 = cards.get(2).getValue();
				int v4 = cards.get(3).getValue();
				int v5 = cards.get(4).getValue();
				if(v1 != v2) {
					return adaptBombsCards(spanCard);
				}
				if(v4 != v5) {
					return adaptBombsCards(spanCard);
				}
				
				if(v2 != v3 && v3 != v4) {
					return adaptBombsCards(spanCard);
				}
				
				if(v3 > v0) {
					return cards;
				}
				return adaptBombsCards(spanCard); 
			}
			
			//如果是多张牌
			for(int i=0; i<cards.size()-3; i++) {
				int v1 = cards.get(i).getValue();
				int v2 = cards.get(i+1).getValue();
				int v3 = cards.get(i+2).getValue();
				int v4 = cards.get(i+3).getValue();
				if(v1 != v2) {
					continue;
				}
				if(v2 != v3) {
					continue;
				}
				if(v1 <= v0) {
					continue;
				}
				if(v3 == v4) {
					i++;
					i++;
					i++;
					continue;
				}
				
				//找到了目标牌
				tempCards.add(cards.get(i));
				tempCards.add(cards.get(i+1));
				tempCards.add(cards.get(i+2));
				//再找一对牌型
				SCard t1 = null;
				SCard t2 = null;
				SCard tt1 = null;
				SCard tt2 = null;
				for(int j=0; j<cards.size()-1; j++) {
					int vv1 = cards.get(j).getValue();
					int vv2 = cards.get(j+1).getValue();
					int vv3 = cards.get(j+2).getValue();
					if(j == i || j == i+1 || j == i+2 ) {
						continue;
					}
					if(vv1 == vv2) {
						if(tt1 == null) {
							tt1 = cards.get(j);
							tt2 = cards.get(j+1);
						}
						
						if(vv2 == vv3) {
							j++;
							j++;
							j++;
							continue;
						}
						else {
							//找到目标单牌
							t1 = cards.get(j);
							t2 = cards.get(j+1);
							break;
						}
					}
				}
				//如果目标单牌没有找到,找最小的
				if(t1 == null && tt1 == null) {
					return adaptBombsCards(spanCard);
				}
				if(t1 != null) {
					tempCards.add(t1);
					tempCards.add(t2);
					return tempCards;
				}
				if(tt1 != null) {
					tempCards.add(tt1);
					tempCards.add(tt2);
					return tempCards;
				}
			}
			return adaptBombsCards(spanCard);
		}
		else if(spanCard.getType() == CardType.order_card) {
			//如果只有1张牌
			if(cards.size() == 1) {
				return null;
			}
			if(cards.size() < spanCard.getCards().size()) {
				return adaptBombsCards(spanCard);
			}
			v0 = spanCard.getCards().get(0).getValue();
			
			//如果牌数相同等
			if(cards.size() == spanCard.getCards().size()) {
				if(cards.get(0).getValue() < v0) {
					return adaptBombsCards(spanCard);	
				}
				
				//判断是否是顺子
				for(int i=0; i<cards.size()-1; i++) {
					int v1 = cards.get(i).getValue();
					int v2 = cards.get(i+1).getValue();
					if(v1 != v2-1) {
						return adaptBombsCards(spanCard);	
					}
				}
				
				//可以打得过对方了
				return cards;
			}
			
			//取一个顺子出来
			for(int i=0; i<cards.size()-1; i++) {
				int v1 = cards.get(i).getValue();
				int v2 = cards.get(i+1).getValue();
				if(v1 != v2-1) {
					tempCards.clear();
					continue;
				}
				if(tempCards.size() == 0) {
					if(v1 <= v0) {
						continue;
					}
				}
				tempCards.add(cards.get(i));
				if(tempCards.size() == spanCard.getCards().size()) {
					return tempCards;
				}
			}
		}
		
		else if(spanCard.getType() == CardType.order_twin) {
			//如果只有1张牌
			if(cards.size() == 1) {
				return null;
			}
			if(cards.size() < spanCard.getCards().size()) {
				return adaptBombsCards(spanCard);
			}
			v0 = spanCard.getCards().get(0).getValue();
			
			//如果牌数相同等
			if(cards.size() == spanCard.getCards().size()) {
				if(cards.get(0).getValue() < v0) {
					return adaptBombsCards(spanCard);	
				}
				
				//判断是否是顺子
				for(int i=0; i<(cards.size()/2)-1; i++) {
					int v1 = cards.get(2*i).getValue();
					int v2 = cards.get(2*i+1).getValue();
					if(v1 != v2) {
						return adaptBombsCards(spanCard);	
					}
					v2 = cards.get(2*i+2).getValue();
					if(v1 != v2-1) {
						return adaptBombsCards(spanCard);	
					}
				}
				
				//可以打得过对方了
				return cards;
			}
			
			//取一个连对出来
			for(int i=0; i<(cards.size()/2)-1; i++) {
				int v1 = cards.get(2*i).getValue();
				int v2 = cards.get(2*i+1).getValue();
				
				if(v1 != v2) {
					tempCards.clear();
					continue;
				}
				v2 = cards.get(2*i+2).getValue();
				if(v1 != v2-1) {
					tempCards.clear();
					continue;	
				}
				if(tempCards.size() == 0) {
					if(v1 <= v0) {
						continue;
					}
				}
				tempCards.add(cards.get(2*i));
				tempCards.add(cards.get(2*i+1));
				if(tempCards.size() == spanCard.getCards().size()) {
					return tempCards;
				}
			}
			
			//再取一遍
			List<SCard> next = new ArrayList<SCard>(cards);
			next.remove(0);
			if(next.size() == spanCard.getCards().size()) {
				return adaptBombsCards(spanCard);
			}
			for(int i=1; i<(next.size()/2)-1; i++) {
				int v1 = next.get(2*i).getValue();
				int v2 = next.get(2*i+1).getValue();
				
				if(v1 != v2) {
					tempCards.clear();
					continue;
				}
				v2 = next.get(2*i+2).getValue();
				if(v1 != v2-1) {
					tempCards.clear();
					continue;	
				}
				if(tempCards.size() == 0) {
					if(v1 <= v0) {
						continue;
					}
				}
				tempCards.add(next.get(2*i));
				tempCards.add(next.get(2*i+1));
				if(tempCards.size() == spanCard.getCards().size()) {
					return tempCards;
				}
			}
		}
		else if(spanCard.getType() == CardType.king_bombs) {
			return null;
		}
		else if(spanCard.getType() == CardType.plain_bombs) {
			//如果对方是4个2
			if(spanCard.getCards().get(0).getValue() == SCard.c20.getValue()) {
				//判断有没有王炸
				if(cards.contains(SCard.STRONG_KING) && cards.contains(SCard.WEAK_KING)) {
					tempCards.add(SCard.STRONG_KING);
					tempCards.add(SCard.WEAK_KING);
					return tempCards;
				}
				return null;
			}
			
			return adaptBombsCards(spanCard);	
		}
		
		//其他牌型统一出炸
		return adaptBombsCards(spanCard);
	}
	
	/***
	 * 找一个炸出来
	 * @return
	 */
	private List<SCard> getBombsCards(SCard from){
		List<SCard> tempCards = new ArrayList<SCard>();
		for(int i=0; i<cards.size()-3; i++) {
			int v0 = cards.get(i).getValue();
			if(from != null) {
				if(from.getValue() > v0) {
					continue;
				}
			}
			
			int v1 = cards.get(i+1).getValue();
			int v2 = cards.get(i+2).getValue();
			int v3 = cards.get(i+3).getValue();
			if(v0 == v1 && v1 == v2 && v2 == v3) {
				tempCards.add(cards.get(i));
				tempCards.add(cards.get(i+1));
				tempCards.add(cards.get(i+2));
				tempCards.add(cards.get(i+3));
				return tempCards;
			}
		}
		
		if(cards.contains(SCard.STRONG_KING) && cards.contains(SCard.WEAK_KING)) {
			tempCards.add(SCard.STRONG_KING);
			tempCards.add(SCard.WEAK_KING);
			return tempCards;
		}
		
		return null;
	}
	
	/***
	 * 适配炸蛋
	 * @return
	 */
	private List<SCard> adaptBombsCards(DdzRuler.SpanCard spanCard){
		//判断是否是敌方
		if(spanCard.getPosition() == this.landlordPosition || position == this.landlordPosition) {
			if(spanCard.getType() == CardType.plain_bombs) {
				return getBombsCards(spanCard.getCards().get(0));
			}
			
			Integer enemyResidueCount = outCards.get(spanCard.getPosition());
			
			if((enemyResidueCount != null && enemyResidueCount<11) || spanCard.getCards().size() > 6) {
				return getBombsCards(null);
			}
			Integer residueCount = outCards.get(position);
			if((residueCount != null && residueCount<6)) {
				return getBombsCards(null);
			}
		}
		
		if(this.cards.size() == 2) {
			return getBombsCards(null);
		}
		if(this.cards.size() == 4) {
			return getBombsCards(null);
		}
		
		return null;
	}
	
	/***
	 * 清空，准备下一局
	 */
	public void clear() {
		cards = null;
		outCards.clear();
		battlefield.clear();
	}
	
	public boolean isGameOver() {
		return cards == null || cards.size() == 0;
	}
}
