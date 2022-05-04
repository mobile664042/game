package com.simple.game.core.domain.cmd.rtn.seat;

import com.simple.game.core.domain.cmd.rtn.RtnCmd;
import com.simple.game.core.domain.cmd.vo.PlayerVo;

import lombok.Data;

@Data
public class RtnGameSeatInfoCmd extends RtnCmd{
	public final static int CODE = 102001;
	
	/***席位主要人员****/
	protected PlayerVo master;
	
	/***(下一轮)主席位继任者****/
	protected PlayerVo nextMaster;
	
	protected boolean stopOnlooker;
	protected boolean stopAssistant;
	/***是否审核通过直播***/
	protected boolean broadcasting;
	/***是否申请直播***/
	protected boolean applyBroadcasted;
	protected int position;
	
	@Override
	public int getCode() {
		return CODE;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
