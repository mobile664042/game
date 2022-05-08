package com.simple.game.core.domain.cmd.rtn.game;

import java.util.List;

import com.simple.game.core.domain.cmd.req.game.ReqGetOnlineListCmd;
import com.simple.game.core.domain.cmd.rtn.RtnCmd;
import com.simple.game.core.domain.cmd.vo.PlayerVo;

import lombok.Data;

@Data
public class RtnGetOnlineListCmd extends RtnCmd{
	private List<PlayerVo> list;
	
	@Override
	public int getCmd() {
		return ReqGetOnlineListCmd.CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
