package com.simple.game.ddz.domain.cmd.push.game.notify;

import java.util.ArrayList;
import java.util.List;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.ddz.domain.manager.GameResultRecord;

import lombok.Data;

@Data
public class NotifyGameOverCmd extends PushCmd{
	
	private final List<ResultItem> list = new ArrayList<ResultItem>();
	
	/***总共翻倍几次***/
	private int doubleFinal;
	
	/***单份结果(unitPrice的对doubleFinal计算结果)****/
	private long singleResult;
	
	/***单价****/
	private int unitPrice;
	
	@Data
	static public class ResultItem{
		private long position; 
		private long changeCoin;
		public ResultItem(long position, long changeCoin) {
			this.position = position;
			this.changeCoin = changeCoin;
		} 
	}
	
	public static NotifyGameOverCmd valueOf(GameResultRecord gameResultRecord) {
		NotifyGameOverCmd notifyCmd = new NotifyGameOverCmd();
		notifyCmd.setDoubleFinal(gameResultRecord.getDoubleFinal());
		notifyCmd.setSingleResult(gameResultRecord.getSingleResult());
		notifyCmd.setUnitPrice(gameResultRecord.getUnitPrice());
		
		for(int position : gameResultRecord.getMap().keySet()) {
			com.simple.game.ddz.domain.manager.GameResultRecord.ResultItem tempItem = gameResultRecord.getMap().get(position);
			ResultItem resultItem = new ResultItem(position, tempItem.getChangeCoin());
			notifyCmd.list.add(resultItem);
		}
		
		return notifyCmd;
	}

	@Override
	public int getCmd() {
		return 151004 + PushCmd.NOTIFY_NUM;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
