package com.simple.game.core.domain.dto.desk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.push.game.PushRewardCmd;
import com.simple.game.core.domain.cmd.req.game.ReqRewardCmd;
import com.simple.game.core.domain.dto.DeskPlugin;
import com.simple.game.core.domain.dto.GameSessionInfo;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.TableDesk;

public class RewardPlugin extends DeskPlugin{
	private final static Logger logger = LoggerFactory.getLogger(RewardPlugin.class);
	
	public static final String PLUGIN_NAME = "reward";
	
	
	public RewardPlugin(TableDesk tableDesk) {
		super(tableDesk, null);
	}
	/***
	 * 在席位中对某个席位进行打赏
	 * @param player
	 */
	public void reward(GameSessionInfo gameSessionInfo, ReqRewardCmd reqCmd) {
		Player player = tableDesk.getPlayer(gameSessionInfo.getPlayerId());
		
		//TODO 扣费
		
		PushRewardCmd pushCmd = reqCmd.valueOfPushRewardCmd();
		pushCmd.setPlayerId(gameSessionInfo.getPlayerId());
		pushCmd.setNickname(player.getNickname());
		pushCmd.setHeadPic(player.getHeadPic());
		//发送广播
		tableDesk.broadcast(pushCmd, gameSessionInfo.getPlayerId());
		logger.info("{}对{}打賞", player.getNickname(), reqCmd.getPositionList());
	}
	

	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}
}
