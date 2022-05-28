package com.simple.game.ddz.domain.service;

import com.simple.game.core.domain.dto.GameSessionInfo;
import com.simple.game.core.domain.service.TableService;
import com.simple.game.ddz.domain.cmd.req.seat.ReqPlayCardCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqReadyNextCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqRobLandlordCmd;
import com.simple.game.ddz.domain.cmd.req.seat.ReqSurrenderCmd;
import com.simple.game.ddz.domain.dto.DdzGameSeat;
import com.simple.game.ddz.domain.manager.DdzGameManager;

import lombok.Getter;

/***
 * 斗地主对外服务
 * 
 * 
 * @author zhibozhang
 *
 */
@Getter
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
	public void readyNext(GameSessionInfo gameSessionInfo, ReqReadyNextCmd reqCmd) {
		DdzGameSeat ddzDeskSeat = (DdzGameSeat)checkAndGetGameSeat(gameSessionInfo.getAddress());
		ddzDeskSeat.readyNext(gameSessionInfo, reqCmd);
	}
	
	
	/***
	 * 抢地主
	 * @param playerId
	 * @param position
	 * @param score		简化操作，暂时不用
	 */
	public void robLandlord(GameSessionInfo gameSessionInfo, ReqRobLandlordCmd reqCmd) {
		DdzGameSeat ddzDeskSeat = (DdzGameSeat)checkAndGetGameSeat(gameSessionInfo.getAddress());
		ddzDeskSeat.robLandlord(gameSessionInfo, reqCmd);
	}
	
	/***
	 * 过牌
	 * @param playerId
	 * @param position
	 * @param cards
	 */
	public void playCard(GameSessionInfo gameSessionInfo, ReqPlayCardCmd reqCmd) {
		DdzGameSeat ddzDeskSeat = (DdzGameSeat)checkAndGetGameSeat(gameSessionInfo.getAddress());
		ddzDeskSeat.playCard(gameSessionInfo, reqCmd);
		
//		DdzDesk tableGame = (DdzDesk)getTableDesk(gameSessionInfo.getAddress());
//		
//		OutParam<SeatPlayer> outParam = OutParam.build();
//		tableGame.playCard(gameSessionInfo.getPlayerId(), reqCmd.getPosition(), reqCmd.getCards(), outParam);
//		PushPlayCardCmd pushCmd = reqCmd.valueOfPushPlayCardCmd();
//		
//		//发送广播
//		tableGame.broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}
	

	/***
	 * 投降认输
	 * 提前结束游戏
	 * 处理方式按com.simple.game.ddz.domain.dto.config.DdzDeskItem.punishSurrenderDoubleCount
	 * @param playerId
	 * @param position
	 */
	public void surrender(GameSessionInfo gameSessionInfo, ReqSurrenderCmd reqCmd) {
		DdzGameSeat ddzDeskSeat = (DdzGameSeat)checkAndGetGameSeat(gameSessionInfo.getAddress());
		ddzDeskSeat.surrender(gameSessionInfo, reqCmd);
		
//		DdzDesk tableGame = (DdzDesk)getTableDesk(gameSessionInfo.getAddress());
//		OutParam<SeatPlayer> outParam = OutParam.build();
//		tableGame.surrender(gameSessionInfo.getPlayerId(), reqCmd.getPosition(), outParam);
//		PushSurrenderCmd pushCmd = reqCmd.valueOfPushSurrenderCmd();
//		pushCmd.setPosition(outParam.getParam().getGameSeat().getPosition());
//		
//		//发送广播
//		tableGame.broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}
	
	
}
