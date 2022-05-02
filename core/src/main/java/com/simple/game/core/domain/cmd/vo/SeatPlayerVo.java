package com.simple.game.core.domain.cmd.vo;

import com.simple.game.core.domain.dto.constant.SeatPost;

import lombok.Data;

@Data
public class SeatPlayerVo extends PlayerVo{
	/**角色***/
	protected SeatPost seatPost;
	
	/**席位**/
	protected int position;
	
}
