package com.simple.game.core.domain.cmd.rtn.seat;

import java.util.List;

import com.simple.game.core.domain.cmd.rtn.RtnCmd;
import com.simple.game.core.domain.cmd.vo.SeatPlayerVo;

import lombok.Data;

@Data
public class RtnGetSeatPlayerListCmd extends RtnCmd{
	private List<SeatPlayerVo> list;
	
	@Override
	public int getCode() {
		return 102002;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}


}
