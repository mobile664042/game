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
			10,11,12,13,
			20,21,22,23,
			30,31,32,33,
			40,41,42,43,
			50,51,52,53,
			60,61,62,63,
			70,71,72,73,
			80,81,82,83,
			90,91,92,93,
			100,101,102,103,
			110,111,112,113,
			120,121,122,123,
			130,131,132,133
	),

	C_54(
			10,11,12,13,
			20,21,22,23,
			30,31,32,33,
			40,41,42,43,
			50,51,52,53,
			60,61,62,63,
			70,71,72,73,
			80,81,82,83,
			90,91,92,93,
			100,101,102,103,
			110,111,112,113,
			120,121,122,123,
			130,131,132,133,
			PokerKind.WEAK_KING,PokerKind.STRONG_KING
	);
	public static final int STRONG_KING = 201;
	public static final int WEAK_KING = 200;
	private List<Integer> cards;
	
	private PokerKind(int ...list) {
		this.cards = new ArrayList<Integer>(list.length);
		for(int card : list) {
			this.cards.add(card);
		}
	}
	
	public boolean isValid(int card) {
		return cards.contains(card);
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
	
	/***
	 * 随机洗牌
	 * @return
	 */
	public List<Integer> shuffleCards(){
		List<Integer> temp = new ArrayList<Integer>(cards);
		List<Integer> list = new ArrayList<Integer>(cards.size());
		Random random = new Random(); 
		int size = cards.size();
		for(int i=size; i>0; i--) {
			int index = random.nextInt(i);
			Integer card = temp.remove(index);
			list.add(card);
		}
		return list;
	}
	
}
