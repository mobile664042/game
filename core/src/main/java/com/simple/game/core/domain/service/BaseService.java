package com.simple.game.core.domain.service;

import java.util.ArrayList;
import java.util.List;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.game.PushChatCmd;
import com.simple.game.core.domain.cmd.push.game.PushConnectCmd;
import com.simple.game.core.domain.cmd.push.game.PushDisconnectCmd;
import com.simple.game.core.domain.cmd.push.game.PushJoinCmd;
import com.simple.game.core.domain.cmd.push.game.PushLeftCmd;
import com.simple.game.core.domain.cmd.req.game.ReqChatCmd;
import com.simple.game.core.domain.cmd.req.game.ReqConnectCmd;
import com.simple.game.core.domain.cmd.req.game.ReqDisconnectCmd;
import com.simple.game.core.domain.cmd.req.game.ReqGetOnlineListCmd;
import com.simple.game.core.domain.cmd.req.game.ReqJoinCmd;
import com.simple.game.core.domain.cmd.req.game.ReqLeftCmd;
import com.simple.game.core.domain.cmd.req.game.ReqPauseCmd;
import com.simple.game.core.domain.cmd.req.game.ReqResumeCmd;
import com.simple.game.core.domain.cmd.rtn.game.RtnGameInfoCmd;
import com.simple.game.core.domain.cmd.rtn.game.RtnGetOnlineListCmd;
import com.simple.game.core.domain.cmd.vo.PlayerVo;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.good.BaseGame;
import com.simple.game.core.domain.manager.GameManager;
import com.simple.game.core.exception.BizException;

import lombok.Getter;
import lombok.ToString;

/***
 * 游戏基础包装组件
 * 
 * 游戏中最基础的组件部分
 * 
 * @author zhibozhang
 *
 */
@Getter
@ToString
public abstract class BaseService{
//	private final static Logger logger = LoggerFactory.getLogger(BaseService.class);
	/***
	 * 游戏管理
	 */
	protected final GameManager gameManager;
	
	public BaseService(GameManager gameManager) {
		this.gameManager = gameManager;
	}
	
	public BaseGame checkAndGet(int playKind, int deskNo) {
		BaseGame baseGame = gameManager.getBaseGame(playKind, deskNo);
		if(baseGame == null) {
			throw new BizException("游戏桌没找到!");
		}
		return baseGame;
	}
	
	/***游戏暂停****/
	public void pause(ReqPauseCmd reqCmd) {
		BaseGame baseGame = checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		if(reqCmd.getSeconds() <= 0) {
			throw new BizException("暂停时长不能小于或等于0");
		}
		baseGame.pause(reqCmd.getSeconds());

		PushCmd pushCmd = reqCmd.valueOfPushPauseCmd();
		baseGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	/***游戏取消暂停(恢复正常)****/
	public void resume(ReqResumeCmd reqCmd) {
		BaseGame baseGame = checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		baseGame.resume();
		PushCmd pushCmd = reqCmd.valueOfPushResumeCmd();
		baseGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}

	/***
	 * 进入游戏
	 */
	public RtnGameInfoCmd join(ReqJoinCmd reqCmd) {
		BaseGame baseGame = checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		Player player = new Player();
		player.setId(reqCmd.getPlayerId());
		player.setNickname(reqCmd.getNickname());
		player.setSex(reqCmd.getSex());
		player.setTelphone(reqCmd.getTelphone());
		player.setHeadPic(String.valueOf(reqCmd.getHeadPic()));
		player.setBcoin(reqCmd.getBcoin());
		
		RtnGameInfoCmd rtnCmd = baseGame.join(player);
		
		//发送广播
		PushJoinCmd pushCmd = reqCmd.valueOfPushJoinCmd();
		pushCmd.setPlayer(player.valueOfPlayerVo());
		baseGame.broadcast(pushCmd, reqCmd.getPlayerId());
		return rtnCmd;
	}
	
	/***
	 * 获取游戏在线玩家
	 */
	public RtnGetOnlineListCmd getOnlineList(ReqGetOnlineListCmd reqCmd) {
		if(reqCmd.getFromPage() < 0) {
			throw new BizException("页码是从0开始");
		}
		
		BaseGame baseGame = checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		List<Player> list = baseGame.getOnlineList(reqCmd.getFromPage());
		
		List<PlayerVo> voList = new ArrayList<PlayerVo>(list.size());
		RtnGetOnlineListCmd rtnCmd = new RtnGetOnlineListCmd();
		rtnCmd.setList(voList);
		for(Player player : list) {
			PlayerVo vo = player.valueOfPlayerVo();
			voList.add(vo);
		}
		return rtnCmd;
	}
	
	/***
	 * 离开游戏
	 */
	public void left(ReqLeftCmd reqCmd) {
		BaseGame baseGame = checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		baseGame.left(reqCmd.getPlayerId(), outParam);
		
		//广播离开信息
		PushLeftCmd pushCmd = reqCmd.valueOfPushLeftCmd();
		pushCmd.setNickname(outParam.getParam().getNickname());
		baseGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	/***聊天****/
	public void chat(ReqChatCmd reqCmd) {
		BaseGame baseGame = checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		baseGame.chat(reqCmd.getPlayerId(), reqCmd.getChat(), outParam);
		
		//广播离开信息
		PushChatCmd pushCmd = reqCmd.valueOfPushChatCmd();
		pushCmd.setNickname(outParam.getParam().getNickname());
		baseGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	/***断网，掉线***/
	public void disconnect(ReqDisconnectCmd reqCmd) {
		BaseGame baseGame = checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		baseGame.disconnect(reqCmd.getPlayerId(), outParam);
		
		//广播离开信息
		PushDisconnectCmd pushCmd = reqCmd.valueOfPushDisconnectCmd();
		pushCmd.setNickname(outParam.getParam().getNickname());
		baseGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	/***掉线重连***/
	public void connected(ReqConnectCmd reqCmd) {
		BaseGame baseGame = checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<Player> outParam = OutParam.build();
		baseGame.connect(reqCmd.getPlayerId(), outParam);
		
		//广播离开信息
		PushConnectCmd pushCmd = reqCmd.valueOfPushConnectCmd();
		baseGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
}
