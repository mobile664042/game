package com.simple.game.core.domain.service.admin;

import com.simple.game.core.domain.cmd.req.game.ReqAdminPauseCmd;
import com.simple.game.core.domain.cmd.req.game.ReqAdminResumeCmd;
import com.simple.game.core.domain.dto.TableDesk;
import com.simple.game.core.domain.dto.desk.ChatPlugin;
import com.simple.game.core.domain.ext.Chat;
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
	/***
	 * 游戏管理
	 */
	protected final GameManager gameManager;
	
	public AdminService(GameManager gameManager) {
		this.gameManager = gameManager;
	}
	
	public TableDesk checkAndGet(int playKind, int deskNo) {
		TableDesk tableDesk = gameManager.getTableDesk(playKind, deskNo);
		if(tableDesk == null) {
			throw new BizException("游戏桌没找到!");
		}
		return tableDesk;
	}
	
	/***中止游戏,强制结算,强退所有人员，销毁游戏****/
	public void destroy(int playKind, int deskNo) {
		checkAndGet(playKind, deskNo);
		gameManager.destroyGameDesk(playKind, deskNo);
	}
	
	/***系统给玩家发送信息****/
	public void chat(int playKind, int deskNo, long playerId, Chat message) {
		TableDesk tableDesk = checkAndGet(playKind, deskNo);
		ChatPlugin chatPlugin = (ChatPlugin)tableDesk.getPlugin(ChatPlugin.PLUGIN_NAME);
		chatPlugin.chat(playerId, message);
	}
	
	/***系统给游戏暂停****/
	public void pause(ReqAdminPauseCmd reqCmd) {
		TableDesk tableDesk = checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		if(reqCmd.getSeconds() <= 0) {
			throw new BizException("暂停时长不能小于或等于0");
		}
		tableDesk.pause(reqCmd.getSeconds(), null);
	}
	/***系统给游戏取消暂停(恢复正常)****/
	public void resume(ReqAdminResumeCmd reqCmd) {
		TableDesk tableDesk = checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		tableDesk.resume(null);
	}
	
	
	public void buildGameDesk(int playKind, int count) {
		gameManager.buildGameDesk(playKind, count);
	}
	
	/***系统强制踢人***/
	public void kickout(int playKind, int deskNo, long playerId) {
		TableDesk tableDesk = checkAndGet(playKind, deskNo);
		tableDesk.kickout(playerId);
	}
}
