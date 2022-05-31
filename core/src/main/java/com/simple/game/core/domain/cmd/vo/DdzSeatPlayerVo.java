package com.simple.game.core.domain.cmd.vo;

import java.util.List;

import com.simple.game.core.domain.dto.constant.SeatPost;

import lombok.Data;

@Data
public class DdzSeatPlayerVo extends PlayerVo{
	/**角色***/
	protected SeatPost seatPost;
	
	/**席位**/
	protected int position;
	
	/**剩余牌**/
	protected int residueCount;
	
	/**如果是明牌，这里面有值的**/
	protected List<Integer> residueCards;
	
}
