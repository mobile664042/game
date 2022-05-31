package com.simple.game.core.domain.dto;

import com.simple.game.core.domain.cmd.vo.DdzSeatPlayerVo;
import com.simple.game.core.domain.dto.constant.SeatPost;

import lombok.Getter;

/***
 * 席位玩家
 * 
 * @author zhibozhang
 *
 */
@Getter
public class SeatPlayer {
	/***玩家****/
	protected Player player;
	
	/***席位号从1号开始****/
	protected GameSeat gameSeat;
	
	/**角色***/
	protected SeatPost seatPost;
	
	
	/**是否已经发送了辅助申请***/
	protected boolean applyAssistanted;
	
	public SeatPlayer(Player player, GameSeat gameSeat, SeatPost seatPost) {
		this.player = player;
		this.gameSeat = gameSeat;
		this.seatPost = seatPost;
	}
	
	
	
	public DdzSeatPlayerVo valueOfSeatPlayerVo() {
		DdzSeatPlayerVo vo = new DdzSeatPlayerVo();
		vo.setId(player.getId());
		vo.setNickname(player.getNickname());
		vo.setGameLevel(player.getGameLevel());
		vo.setExpValue(player.getExpValue());
		vo.setVipLevel(player.getVipLevel());
		vo.setHeadPic(player.getHeadPic());
		
		vo.setPosition(gameSeat.getPosition());
		vo.setSeatPost(seatPost);
		return vo;
	}



	public void setApplyAssistanted(boolean applyAssistanted) {
		this.applyAssistanted = applyAssistanted;
	}
	
}
