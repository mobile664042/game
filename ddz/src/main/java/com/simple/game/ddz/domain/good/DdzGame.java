package com.simple.game.ddz.domain.good;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.dto.BaseDesk;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.SeatPlayer;
import com.simple.game.core.domain.good.TableGame;
import com.simple.game.core.exception.BizException;
import com.simple.game.ddz.domain.cmd.push.seat.PushPlayCardCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushReadyNextCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushSurrenderCmd;
import com.simple.game.ddz.domain.dto.DdzDesk;
import com.simple.game.ddz.domain.dto.config.DdzDeskItem;
import com.simple.game.ddz.domain.dto.config.DdzGameItem;

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
	public DdzGame(DdzGameItem gameItem, DdzDeskItem deskItem) {
		super(gameItem, deskItem);
	}
	
	@Override
	protected BaseDesk buildDesk(){
		return new DdzDesk(this);
	}
	
	/***
	 * 当前轮结束，准备下一轮
	 * @param playerId
	 * @param position
	 */
	public void readyNext(long playerId, int position, OutParam<SeatPlayer> outParam) {
		this.operatorVerfy();
		PushReadyNextCmd result = getDdzDesk().readyNext(playerId, position, outParam);
		this.broadcast(result, playerId);
		logger.info("{}已准备进行新一轮了,所在席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), baseDesk.getAddrNo(), position);
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
	public void robLandlord(long playerId, int position, int score, OutParam<SeatPlayer> outParam) {
		this.operatorVerfy();
		getDdzDesk().robLandlord(playerId, position, score, outParam);
		logger.info("{}抢地主,所在席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), baseDesk.getAddrNo(), position);
	}
	
	/***
	 * 过牌
	 * @param playerId
	 * @param position
	 * @param cards
	 */
	public void playCard(long playerId, int position, List<Integer> cards, OutParam<SeatPlayer> outParam) {
		this.operatorVerfy();
		PushPlayCardCmd result = getDdzDesk().playCard(playerId, position, cards, outParam);
		this.broadcast(result, playerId);
		logger.info("{}出牌,所在席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), baseDesk.getAddrNo(), position);
	}
	

	/***
	 * 投降认输
	 * 直接参考@com.simple.game.core.domain.dto.config.ddz.ExtGameItem.punishSurrenderDoubleCount处理
	 * @param playerId
	 * @param position
	 */
	public void surrender(long playerId, int position, OutParam<SeatPlayer> outParam) {
		this.operatorVerfy();
		PushSurrenderCmd result = getDdzDesk().surrender(playerId, position, outParam);
		this.broadcast(result, playerId);
		logger.info("{}投降认输,所在席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), baseDesk.getAddrNo(), outParam.getParam().getGameSeat().getPosition());
	}
	
	
	protected DdzDesk getDdzDesk() {
		return (DdzDesk)this.baseDesk;
	}
	protected DdzDeskItem getDdzDeskItem() {
		return (DdzDeskItem)this.deskItem;
	}
	
}
