package com.simple.game.core.domain.dto;

import com.simple.game.core.domain.cmd.vo.SeatPlayerVo;
import com.simple.game.core.domain.dto.constant.SeatPost;

import lombok.Getter;
import lombok.ToString;

/***
 * 席位玩家
 * 
 * @author zhibozhang
 *
 */
//@Data
@Getter
@ToString
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
	
	
	
	public SeatPlayerVo valueOfSeatPlayerVo() {
		SeatPlayerVo vo = new SeatPlayerVo();
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
