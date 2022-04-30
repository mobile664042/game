package com.simple.game.core.domain.dto;

import java.util.List;

import com.simple.game.core.domain.cmd.push.PushApplyManagerCmd;
import com.simple.game.core.domain.cmd.push.PushChangeManagerCmd;
import com.simple.game.core.domain.cmd.push.PushChatCmd;
import com.simple.game.core.domain.cmd.push.PushDisconnectCmd;
import com.simple.game.core.domain.cmd.push.PushJoinedCmd;
import com.simple.game.core.domain.cmd.push.PushLeftCmd;
import com.simple.game.core.domain.cmd.push.PushNotifyApplyManagerCmd;
import com.simple.game.core.domain.cmd.push.PushRewardCmd;
import com.simple.game.core.domain.ext.Chat;
import com.simple.game.core.domain.ext.Gift;

import lombok.Data;
import lombok.ToString;

/***
 * 游戏玩家
 * 
 * @author zhibozhang
 *
 */
@Data
@ToString
public class Player {
	private long id;
	/**名称***/
	private String nickname;
	/**游戏等级***/
	private int gameLevel;
	/**当前经验值***/
	private int expValue;
	/**vip等级***/
	private int vipLevel;
	
	/**头像***/
	private String headPic;
	
//	/**银行已存入币(库存，变动次数少)***/
//	private long bankCoin;
	
	/**带入币(桌面币，不一定等于bankCoin, 一般使用这个做游戏计算，变动次数多)***/
	private long bcoin;
	
	/**游戏地址(通过它可以快速的找到玩家)***/
	private AddressNo address;
	
	/**在线信息***/
	private OnlineInfo online;
	
//	/***扩展属性****/
//	private Object extConfig;
	
	public long addCoin(long change) {
		bcoin += change;
		return bcoin;
	}
	
	public PushJoinedCmd toPushJoinedCmd() {
		//TODO 
		return null;
	}
	
	public PushLeftCmd toPushLeftCmd() {
		//TODO 
		return null;
	}
	
	public PushChatCmd toPushChatCmd(Chat message) {
		
		//TODO 
		return null;
	}
	public List<PushChatCmd> toPushChatCmd(List<Integer> positionList, Chat message) {
		
		//TODO 
		return null;
	}
	
	

	public List<PushRewardCmd> toPushRewardCmd(List<Integer> positionList, Gift gift) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public PushApplyManagerCmd toPushApplyManagerCmd() {
		// TODO Auto-generated method stub
		return null;
	}
	

	public PushNotifyApplyManagerCmd toPushNotifyApplyManagerCmd() {
		// TODO Auto-generated method stub
		return null;
	}
	public PushChangeManagerCmd toPushChangeManagerCmd(Long playerId) {
		return null;
	}
	
	public PushDisconnectCmd toPushDisconnectCmd() {
		return null;
	}
	

	public OnlineUserInfo toOnlineUserInfo() {
		return null;
	}
}
