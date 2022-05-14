package com.simple.game.core.domain.dto;

import com.simple.game.core.domain.cmd.vo.PlayerVo;

import lombok.Data;

/***
 * 游戏玩家
 * 
 * @author zhibozhang
 *
 */
@Data
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
	
    private int sex;
    private String telphone;
    
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
	
	public PlayerVo valueOfPlayerVo() {
		PlayerVo vo = new PlayerVo();
		vo.setId(id);
		vo.setNickname(nickname);;
		vo.setGameLevel(gameLevel);
		vo.setExpValue(expValue);
		vo.setVipLevel(vipLevel);
		vo.setHeadPic(headPic);
		return vo;
	}
	
	

	public OnlineUserInfo toOnlineUserInfo() {
		return null;
	}
}
