package com.simple.game.core.domain.dto;

import java.util.List;

import com.simple.game.core.domain.cmd.push.PushApplyBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.push.PushApproveBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.push.PushBootAssistantCmd;
import com.simple.game.core.domain.cmd.push.PushBootOnlookerCmd;
import com.simple.game.core.domain.cmd.push.PushBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.push.PushCancelBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.push.PushChatCmd;
import com.simple.game.core.domain.cmd.push.PushNotifyApplyAssistantCmd;
import com.simple.game.core.domain.cmd.push.PushRewardCmd;
import com.simple.game.core.domain.cmd.push.PushSetSeatSuccessorCmd;
import com.simple.game.core.domain.cmd.push.PushSitdownCmd;
import com.simple.game.core.domain.cmd.push.PushStandupCmd;
import com.simple.game.core.domain.cmd.push.PushStopAssistantCmd;
import com.simple.game.core.domain.cmd.push.PushStopOnlookerCmd;
import com.simple.game.core.domain.dto.constant.SeatPost;
import com.simple.game.core.domain.ext.Chat;
import com.simple.game.core.domain.ext.Gift;

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
	
	public PushSitdownCmd toPushSitdownCmd() {
		//TODO 
		return null;
	}
	public PushStopAssistantCmd toPushStopAssistantCmd() {
		//TODO 
		return null;
	}
	public PushStopOnlookerCmd toPushStopOnlookerCmd() {
		//TODO 
		return null;
	}
	public PushBootAssistantCmd toPushBootAssistantCmd() {
		//TODO 
		return null;
	}
	public PushBootOnlookerCmd toPushBootOnlookerCmd() {
		//TODO 
		return null;
	}
	
	public PushStandupCmd toPushStandupCmd() {
		//TODO 
		return null;
	}
	
	public PushSetSeatSuccessorCmd toPushRobSeatMasterCmd() {
		//TODO 
		return null;
	}
//	
	public PushApproveBroadcastLiveCmd toPushApproveBroadcastLiveCmd() {
		//TODO 
		return null;
	}
	
	public PushApplyBroadcastLiveCmd toPushApplyBroadcastLiveCmd() {
		//TODO 
		return null;
	}

	public PushCancelBroadcastLiveCmd toPushCancelBroadcastLiveCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushBroadcastLiveCmd toPushBroadcastLiveCmd(byte[] data) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<PushRewardCmd> toPushRewardCmd(List<Integer> positionList, Gift gift) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<PushChatCmd> toPushChatCmd(List<Integer> positionList, Chat message) {
		// TODO Auto-generated method stub
		return null;
	}
	public PushNotifyApplyAssistantCmd toPushNotifyApplyAssistantCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushChatCmd toPushChatCmd(Chat message) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
