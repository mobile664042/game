package com.simple.game.ddz.domain.service;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.dto.SeatPlayer;
import com.simple.game.core.domain.service.TableService;
import com.simple.game.ddz.domain.cmd.push.seat.PushPlayCardCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushReadyNextCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushRobLandlordCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushSurrenderCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqPlayCardCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqReadyNextCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqRobLandlordCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqSurrenderCmd;
import com.simple.game.ddz.domain.good.DdzGame;
import com.simple.game.ddz.domain.manager.DdzGameManager;

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
public class DdzService extends TableService{
//	private final static Logger logger = LoggerFactory.getLogger(DdzService.class);
	
	public DdzService(DdzGameManager gameManager) {
		super(gameManager);
	}

	
	/***
	 * 当前轮结束，准备下一轮
	 * @param playerId
	 * @param position
	 */
	public void readyNext(ReqReadyNextCmd reqCmd) {
		DdzGame tableGame = (DdzGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<SeatPlayer> outParam = OutParam.build();
		tableGame.readyNext(reqCmd.getPlayerId(), reqCmd.getPosition(), outParam);
		PushReadyNextCmd pushCmd = reqCmd.valueOfPushReadyNextCmd();
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	
	/***
	 * 抢地主
	 * @param playerId
	 * @param position
	 * @param score		简化操作，暂时不用
	 */
	public void robLandlord(ReqRobLandlordCmd reqCmd) {
		DdzGame tableGame = (DdzGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<SeatPlayer> outParam = OutParam.build();
		tableGame.robLandlord(reqCmd.getPlayerId(), reqCmd.getPosition(), reqCmd.getScore(), outParam);
		PushRobLandlordCmd pushCmd = reqCmd.valueOfPushRobLandlordCmd();
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	/***
	 * 过牌
	 * @param playerId
	 * @param position
	 * @param cards
	 */
	public void playCard(ReqPlayCardCmd reqCmd) {
		DdzGame tableGame = (DdzGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<SeatPlayer> outParam = OutParam.build();
		tableGame.playCard(reqCmd.getPlayerId(), reqCmd.getPosition(), reqCmd.getCards(), outParam);
		PushPlayCardCmd pushCmd = reqCmd.valueOfPushPlayCardCmd();
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	

	/***
	 * 投降认输
	 * 直接参考@com.simple.game.core.domain.dto.config.ddz.ExtGameItem.punishSurrenderDoubleCount处理
	 * @param playerId
	 * @param position
	 */
	public void surrender(ReqSurrenderCmd reqCmd) {
		DdzGame tableGame = (DdzGame)checkAndGet(reqCmd.getPlayKind(), reqCmd.getDeskNo());
		OutParam<SeatPlayer> outParam = OutParam.build();
		tableGame.surrender(reqCmd.getPlayerId(), reqCmd.getPosition(), outParam);
		PushSurrenderCmd pushCmd = reqCmd.valueOfPushSurrenderCmd();
		
		//发送广播
		tableGame.broadcast(pushCmd, reqCmd.getPlayerId());
	}
	
	
}
