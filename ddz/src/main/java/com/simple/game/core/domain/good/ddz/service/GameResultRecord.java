package com.simple.game.core.domain.good.ddz.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.simple.game.core.domain.dto.constant.ddz.DoubleKind;

import lombok.Data;
import lombok.ToString;

/***
 * 
 * 记录结果的变化
 * 
 * @author zhibozhang
 *
 */
@Data
@ToString
public class GameResultRecord {
	/***
	 * key席位
	 */
	private final LinkedHashMap<Integer, ResultItem> map = new LinkedHashMap<Integer, ResultItem>();
	
	private String batchNo = "bno." + System.currentTimeMillis();
	
	private final List<Integer> cards = new ArrayList<Integer>();
	
	/***地主位***/
	private int landlordPosition;
	
	/***总共翻倍几次***/
	private int doubleCount;
	
	/***游戏加倍玩法****/
	private DoubleKind doubleKind;
	
	/***单价****/
	private int unitPrice;
	
	@Data
	static public class ResultItem{
		private long playerId; 
		private long changeCoin;
		public ResultItem(long playerId, long changeCoin) {
			this.playerId = playerId;
			this.changeCoin = changeCoin;
		} 
	}
	
}
