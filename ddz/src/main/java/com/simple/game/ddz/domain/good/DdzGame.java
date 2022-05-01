package com.simple.game.ddz.domain.good;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.dto.BaseDesk;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.SeatPlayer;
import com.simple.game.core.domain.dto.config.DeskItem;
import com.simple.game.core.domain.dto.config.GameItem;
import com.simple.game.core.domain.good.TableGame;
import com.simple.game.core.exception.BizException;
import com.simple.game.ddz.domain.cmd.push.PushPlayCardCmd;
import com.simple.game.ddz.domain.cmd.push.PushReadyNextCmd;
import com.simple.game.ddz.domain.cmd.push.PushRobLandlordCmd;
import com.simple.game.ddz.domain.cmd.push.PushSurrenderCmd;
import com.simple.game.ddz.domain.dto.DdzDesk;
import com.simple.game.ddz.domain.dto.config.DdzDeskItem;

import lombok.ToString;

/***
 * 斗地主
 * 
 * @author zhibozhang
 *
 */
@ToString
public class DdzGame extends TableGame{
	private static Logger logger = LoggerFactory.getLogger(DdzGame.class);
	
	@Override
	protected void preInit(GameItem gameItem, DeskItem deskItem) {
		if(gameItem == null || deskItem == null) {
			throw new BizException("无效的参数");
		}
	}
	
	@Override
	/***游戏初使化****/
	protected BaseDesk buildDesk(){
		return new DdzDesk(this);
	}
	
	
	/***
	 * 当前轮结束，准备下一轮
	 * @param playerId
	 * @param position
	 */
	public void readyNext(long playerId, int position) {
		this.operatorVerfy();
		OutParam<SeatPlayer> outParam = OutParam.build();
		PushReadyNextCmd result = getDdzDesk().readyNext(playerId, position, outParam);
		this.broadcast(result, playerId);
		logger.info("{}已准备进行一轮了,所在席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), deskItem.getNumber(), outParam.getParam().getGameSeat().getPosition());
	}
	@Override
	protected void preQuickSitdown(long playerId) {
		Player player = this.getDdzDesk().getPlayerMap().get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在当前游戏房间", playerId));
		}
		//判断游戏币够不够
		if(player.getBcoin() < this.getDdzDeskItem().getMinSitdownCoin()) {
			throw new BizException(String.format("%s的钱不够%s,无法坐下主席位", playerId, this.getDdzDeskItem().getMinSitdownCoin()));
		}
	}
	
	@Override
	protected void onApproveApplyAssistant(SeatPlayer player) {
	}
	@Override
	protected void onDestroy() {
	}
	@Override
	protected void preJoin(Player player) {
	}
	
	
	@Override
	protected boolean onScan() {
		return this.getDdzDesk().onScan();
	}
	
	
	/***
	 * 抢地主
	 * @param playerId
	 * @param position
	 * @param score		简化操作，暂时不用
	 */
	public void robLandlord(long playerId, int position, int score) {
		this.operatorVerfy();
		OutParam<SeatPlayer> outParam = OutParam.build();
		PushRobLandlordCmd result = getDdzDesk().robLandlord(playerId, position, score, outParam);
		if(result != null) {
			this.broadcast(result, playerId);
		}
		logger.info("{}抢地主,所在席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), deskItem.getNumber(), outParam.getParam().getGameSeat().getPosition());
	}
	
	/***
	 * 过牌
	 * @param playerId
	 * @param position
	 * @param cards
	 */
	public void playCard(long playerId, int position, List<Integer> cards) {
		this.operatorVerfy();
		OutParam<SeatPlayer> outParam = OutParam.build();
		PushPlayCardCmd result = getDdzDesk().playCard(playerId, position, cards, outParam);
		this.broadcast(result, playerId);
		logger.info("{}出牌,所在席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), deskItem.getNumber(), outParam.getParam().getGameSeat().getPosition());
	}
	

	/***
	 * 投降认输
	 * 直接参考@com.simple.game.core.domain.dto.config.ddz.ExtGameItem.punishSurrenderDoubleCount处理
	 * @param playerId
	 * @param position
	 */
	public void surrender(long playerId, int position) {
		this.operatorVerfy();
		OutParam<SeatPlayer> outParam = OutParam.build();
		PushSurrenderCmd result = getDdzDesk().surrender(playerId, position, outParam);
		this.broadcast(result, playerId);
		logger.info("{}投降认输,所在席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), deskItem.getNumber(), outParam.getParam().getGameSeat().getPosition());
	}
	
	
	protected DdzDesk getDdzDesk() {
		return (DdzDesk)this.baseDesk;
	}
	protected DdzDeskItem getDdzDeskItem() {
		return (DdzDeskItem)this.deskItem;
	}
	
}
