package com.simple.game.core.domain.cmd.rtn.seat;

import java.util.List;

import com.simple.game.core.domain.cmd.req.seat.ReqGetAssistantListCmd;
import com.simple.game.core.domain.cmd.rtn.RtnCmd;
import com.simple.game.core.domain.cmd.vo.DdzSeatPlayerVo;

import lombok.Data;

@Data
public class RtnGetAssistantListCmd extends RtnCmd{
	private List<DdzSeatPlayerVo> list;
	
	@Override
	public int getCmd() {
		return ReqGetAssistantListCmd.CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
