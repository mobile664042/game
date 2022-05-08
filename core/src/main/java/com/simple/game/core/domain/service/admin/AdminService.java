package com.simple.game.core.domain.service.admin;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.game.PushLeftCmd;
import com.simple.game.core.domain.cmd.push.game.notify.PushSysChatCmd;
import com.simple.game.core.domain.cmd.req.game.ReqAdminPauseCmd;
import com.simple.game.core.domain.cmd.req.game.ReqAdminResumeCmd;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.ext.Chat;
import com.simple.game.core.domain.good.TableGame;
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
	
	public TableGame checkAndGet(int playKind, int deskNo) {
		TableGame tableGame = gameManager.getTableGame(playKind, deskNo);
		if(tableGame == null) {
			throw new BizException("游戏桌没找到!");
		}
		return tableGame;
	}
	
	/***中止游戏,强制结算,强退所有人员，销毁游戏****/
	public void destroy(int playKind, int deskNo) {
		checkAndGet(playKind, deskNo);
		gameManager.destroyGameDesk(playKind, deskNo);
	}
	
	/***系统给玩家发送信息****/
	public void chat(int playKind, int deskNo, long playerId, Chat message) {
		TableGame tableGame = checkAndGet(playKind, deskNo);
		
		OutParam<Player> outParam = OutParam.build();
		tableGame.chat(playerId, message, outParam);
		
		//发送给玩家
		PushSysChatCmd pushCmd = new PushSysChatCmd();
		pushCmd.setChat(message);
		outParam.getParam().getOnline().push(pushCmd);
	}
	
	/***系统给游戏暂停****/
	public void pause(ReqAdminPauseCmd reqCmd) {
		TableGame tableGame = checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		if(reqCmd.getSeconds() <= 0) {
			throw new BizException("暂停时长不能小于或等于0");
		}
		tableGame.pause(reqCmd.getSeconds());

		PushCmd pushCmd = reqCmd.valueOfPushPauseCmd();
		tableGame.broadcast(pushCmd);
	}
	/***系统给游戏取消暂停(恢复正常)****/
	public void resume(ReqAdminResumeCmd reqCmd) {
		TableGame tableGame = checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		tableGame.resume();
		PushCmd pushCmd = reqCmd.valueOfPushResumeCmd();
		tableGame.broadcast(pushCmd);
	}
	
	
	public void buildGameDesk(int playKind, int count) {
		gameManager.buildGameDesk(playKind, count);
	}
	
	/***系统强制踢人***/
	public void kickout(int playKind, int deskNo, long playerId) {
		TableGame tableGame = checkAndGet(playKind, deskNo);
		OutParam<Player> outParam = OutParam.build();
		tableGame.kickout(playerId, outParam);
		
		PushLeftCmd pushCmd = new PushLeftCmd();
		pushCmd.setPlayKind(playKind);
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayerId(playerId);
		pushCmd.setNickname(outParam.getParam().getNickname());
		pushCmd.setHeadPic(outParam.getParam().getHeadPic());
		tableGame.broadcast(pushCmd);
	}
}
