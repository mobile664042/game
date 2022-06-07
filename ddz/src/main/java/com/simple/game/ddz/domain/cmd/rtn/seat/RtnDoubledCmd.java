package com.simple.game.ddz.domain.cmd.rtn.seat;

import com.simple.game.core.domain.cmd.rtn.RtnCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqDoubledCmd;

import lombok.Data;

/***
 * 
 * 加倍
 * 
 * @author Administrator
 *
 */
@Data
public class RtnDoubledCmd extends RtnCmd{
	/***总共翻倍几次***/
	protected int doubleFinal;
	protected boolean next;
	
	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCmd() {
		return ReqDoubledCmd.CMD;
	}
}
