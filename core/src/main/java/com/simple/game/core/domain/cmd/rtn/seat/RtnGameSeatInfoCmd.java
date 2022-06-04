package com.simple.game.core.domain.cmd.rtn.seat;

import java.util.List;

import com.simple.game.core.domain.cmd.req.seat.ReqSitdownCmd;
import com.simple.game.core.domain.cmd.rtn.RtnCmd;
import com.simple.game.core.domain.cmd.vo.PlayerVo;
import com.simple.game.core.domain.dto.constant.SeatPost;

import lombok.Data;

@Data
public class RtnGameSeatInfoCmd extends RtnCmd{
	/***席位主要人员****/
	protected PlayerVo master;
	
	/***(下一轮)主席位继任者****/
	protected PlayerVo nextMaster;
	
	/**加入后的角色***/
	protected SeatPost seatPost;
	
	protected boolean ready;
	protected boolean stopOnlooker;
	protected boolean stopAssistant;
	/***是否审核通过直播***/
	protected boolean broadcasting;
	/***是否申请直播***/
	protected boolean applyBroadcasted;
	protected int position;
	private List<Integer> cards;
	
	@Override
	public int getCmd() {
		return ReqSitdownCmd.CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
