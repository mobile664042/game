package com.simple.game.core.domain.cmd.rtn.game;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.simple.game.core.domain.cmd.req.game.ReqJoinCmd;
import com.simple.game.core.domain.cmd.rtn.RtnCmd;
import com.simple.game.core.domain.cmd.vo.PlayerVo;

import lombok.Data;

@Data
public class RtnGameInfoCmd extends RtnCmd{
	/***所在游戏中的定位(可能为空)***/
	private Integer playKind;
	
	/***所在游戏中的地址
	 * 用户解决重新进入的问题
	 * 
	 * 玩法、桌号、席位三层地址
	 * @45@78@1
	 * ***/
	private String address;
	
	
	/***
	 * 暂停时长(毫秒)
	 */
	private long pauseMs;
	
	/***
	 * 管理员的id
	 */
	private Long managerId;
	private long playerId;
	
	/***
	 * 席位上的主席位玩家
	 * key position(必须使用字符串，不然json解析会出bug)
	 */
	protected HashMap<String, ? extends PlayerVo> seatPlayingMap;
	
	@Override
	public int getCmd() {
		return ReqJoinCmd.CMD;
	}

	@Override
	public String toLogStr() {
		return JSON.toJSONString(this);
	}


}
