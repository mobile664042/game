package com.simple.game.core.domain.good.ddz.service;

import java.util.LinkedHashMap;

import lombok.Data;
import lombok.ToString;

/***
 * 
 * 记录逃跑处理结果
 * 
 * @author zhibozhang
 *
 */
@Data
@ToString
public class EscapeResultRecord {
	/***
	 * key席位
	 */
	private final LinkedHashMap<Integer, ResultItem> map = new LinkedHashMap<Integer, ResultItem>();
	
	private String batchNo;
	
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
