package com.simple.game.ddz.domain.manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.simple.game.ddz.domain.dto.constant.ddz.DoubleKind;

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
	
	private String batchNo = "bno.gr." + System.currentTimeMillis();
	
	private final List<Integer> cards = new ArrayList<Integer>();
	
	/***地主位***/
	private int landlordPosition;
	
	/***总共翻倍几次***/
	private int doubleCount;
	
	/***游戏加倍玩法****/
	private DoubleKind doubleKind;
	
	/***单价****/
	private int unitPrice;
	
	/***单份结果(unitPrice的对doubleCount计算结果)****/
	private long singleResult;
	
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
