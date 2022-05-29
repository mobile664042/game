package com.simple.game.core.domain.dto.constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/****
 * 扑克种类
 * 
 * 百位、十位数表示牌 A:1 J:11 Q:12 K:13, 数字保持不变
 * 个位数表示花色: 0黑桃 1红桃 2梅花 3方片
 * 200 表示小王
 * 201 表示大王
 * 
 * 例如：红桃8: 81， 黑桃Q: 120， 
 * 
 * @author zhibozhang
 *
 */
public enum PokerKind {
	
	
	C_52(
			SCard.c10, SCard.c11, SCard.c12, SCard.c13,
			SCard.c20, SCard.c21, SCard.c22, SCard.c23,
			SCard.c30, SCard.c31, SCard.c32, SCard.c33,
			SCard.c40, SCard.c41, SCard.c42, SCard.c43,
			SCard.c50, SCard.c51, SCard.c52, SCard.c53,
			SCard.c60, SCard.c61, SCard.c62, SCard.c63,
			SCard.c70, SCard.c71, SCard.c72, SCard.c73,
			SCard.c80, SCard.c81, SCard.c82, SCard.c83,
			SCard.c90, SCard.c91, SCard.c92, SCard.c93,
			SCard.c100, SCard.c101, SCard.c102, SCard.c103,
			SCard.c110, SCard.c111, SCard.c112, SCard.c113,
			SCard.c120, SCard.c121, SCard.c122, SCard.c123,
			SCard.c130, SCard.c131, SCard.c132, SCard.c133
	),

	C_54(
			SCard.c10, SCard.c11, SCard.c12, SCard.c13,
			SCard.c20, SCard.c21, SCard.c22, SCard.c23,
			SCard.c30, SCard.c31, SCard.c32, SCard.c33,
			SCard.c40, SCard.c41, SCard.c42, SCard.c43,
			SCard.c50, SCard.c51, SCard.c52, SCard.c53,
			SCard.c60, SCard.c61, SCard.c62, SCard.c63,
			SCard.c70, SCard.c71, SCard.c72, SCard.c73,
			SCard.c80, SCard.c81, SCard.c82, SCard.c83,
			SCard.c90, SCard.c91, SCard.c92, SCard.c93,
			SCard.c100, SCard.c101, SCard.c102, SCard.c103,
			SCard.c110, SCard.c111, SCard.c112, SCard.c113,
			SCard.c120, SCard.c121, SCard.c122, SCard.c123,
			SCard.c130, SCard.c131, SCard.c132, SCard.c133,
			SCard.WEAK_KING,SCard.STRONG_KING
	);
	
//	public static final int STRONG_KING = 201;
//	public static final int WEAK_KING = 200;
	private List<SCard> cards;
	private List<Integer> faceList;
	
	private PokerKind(SCard ...list) {
		this.cards = new ArrayList<SCard>(list.length);
		this.faceList = new ArrayList<Integer>(list.length);
		for(SCard card : list) {
			this.cards.add(card);
			faceList.add(card.getFace());
		}
	}
	
	public boolean isValid(SCard card) {
		return cards.contains(card);
	}
	public boolean isValid(int card) {
		return faceList.contains(card);
	}
	
	public static boolean isPokerCard(int card) {
		if(card == 200 || card == 201) {
			return true;
		}
		if(card < 10 || card > 201) {
			return false;
		}
		if(card > 133 && card < 200) {
			return false;
		}
		int color = card % 10;
		if(color > 4) {
			return false;
		}
		return true;
	}
	
	public static List<Integer> convertFaceList(List<SCard> list) {
		List<Integer> cards = new ArrayList<Integer>(list.size());
		for(SCard item : list) {
			cards.add(item.getFace());
		}
		return cards;
	}
	
	/***
	 * 随机洗牌
	 * @return
	 */
	public List<SCard> shuffleCards(){
		List<SCard> temp = new ArrayList<SCard>(cards);
		List<SCard> list = new ArrayList<SCard>(cards.size());
		Random random = new Random(); 
		int size = cards.size();
		for(int i=size; i>0; i--) {
			int index = random.nextInt(i);
			SCard card = temp.remove(index);
			list.add(card);
		}
		return list;
	}
	
}
