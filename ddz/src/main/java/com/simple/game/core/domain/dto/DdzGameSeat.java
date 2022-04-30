package com.simple.game.core.domain.dto;

import com.simple.game.core.domain.cmd.rtn.RtnGameSeatCmd;
import com.simple.game.core.domain.cmd.rtn.ddz.RtnDdzGameSeatCmd;

import lombok.Data;

/***
 * 游戏桌
 * 
 * @author zhibozhang
 *
 */
@Data
public class DdzGameSeat extends GameSeat{ 
//	private List<Integer> cards = new ArrayList<Integer>(20); 

	public DdzGameSeat(BaseDesk desk, int position) {
		super(desk, position);
	}
	
	public DdzGameSeat(GameSeat gameSeat) {
		super(gameSeat.getDesk(), gameSeat.getPosition());
	}

	/***
	 * 是否准备了好没有
	 */
	private boolean ready = true;
	

	public RtnGameSeatCmd getRtnGameSeatCmd() {
		//TODO 
		return new RtnDdzGameSeatCmd();
	}
}
