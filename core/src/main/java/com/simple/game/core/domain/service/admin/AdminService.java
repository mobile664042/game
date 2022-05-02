package com.simple.game.core.domain.service.admin;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.push.game.PushLeftCmd;
import com.simple.game.core.domain.cmd.push.game.notify.PushDestroyCmd;
import com.simple.game.core.domain.cmd.push.game.notify.PushSysChatCmd;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.ext.Chat;
import com.simple.game.core.domain.good.BaseGame;
import com.simple.game.core.domain.manager.GameManager;
import com.simple.game.core.exception.BizException;

import lombok.Getter;
import lombok.ToString;

/***
 * 管理部分
 * @author zhibozhang
 *
 */
@Getter
@ToString
public abstract class AdminService{
//	private final static Logger logger = LoggerFactory.getLogger(AdminService.class);
	/***
	 * 游戏管理
	 */
	protected final GameManager gameManager;
	
	public AdminService(GameManager gameManager) {
		this.gameManager = gameManager;
	}
	
	public BaseGame checkAndGet(int playKind, int deskNo) {
		BaseGame baseGame = gameManager.getBaseGame(playKind, deskNo);
		if(baseGame == null) {
			throw new BizException("游戏桌没找到!");
		}
		return baseGame;
	}
	
	/***中止游戏,强制结算,强退所有人员，销毁游戏****/
	public void destroy(int playKind, int deskNo) {
		BaseGame baseGame = checkAndGet(playKind, deskNo);
		
		PushDestroyCmd pushCmd = new PushDestroyCmd();
		pushCmd.setPlayKind(playKind);
		pushCmd.setDeskNo(deskNo);
		baseGame.broadcast(pushCmd, false);
		
		//销毁游桌
		baseGame.destroy();
	}
	
	/***系统给玩家发送信息****/
	public void chat(int playKind, int deskNo, long playerId, Chat message) {
		BaseGame baseGame = checkAndGet(playKind, deskNo);
		
		OutParam<Player> outParam = OutParam.build();
		baseGame.chat(playerId, message, outParam);
		
		//发送给玩家
		PushSysChatCmd pushCmd = new PushSysChatCmd();
		pushCmd.setChat(message);
		outParam.getParam().getOnline().push(pushCmd);
	}
	
	public void buildGameDesk(int playKind, int count) {
		gameManager.buildGameDesk(playKind, count);
		
		//TODO 先不广播了
//		PushBuildCmd pushCmd = new PushBuildCmd();
//		pushCmd.setPlayKind(playKind);
//		baseGame.broadcast(pushCmd, false);
	}
	
	/***系统强制踢人***/
	public void kickout(int playKind, int deskNo, long playerId) {
		BaseGame baseGame = checkAndGet(playKind, deskNo);
		baseGame.kickout(playerId);
		
		PushLeftCmd pushCmd = new PushLeftCmd();
		pushCmd.setPlayKind(playKind);
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayerId(playerId);
		baseGame.broadcast(pushCmd);
	}
}
