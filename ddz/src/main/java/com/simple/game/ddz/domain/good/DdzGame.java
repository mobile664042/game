package com.simple.game.ddz.domain.good;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.rtn.game.RtnGameInfoCmd;
import com.simple.game.core.domain.cmd.rtn.seat.RtnGameSeatInfoCmd;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.SeatPlayer;
import com.simple.game.core.domain.dto.TableDesk;
import com.simple.game.core.domain.good.TableGame;
import com.simple.game.core.exception.BizException;
import com.simple.game.ddz.domain.cmd.push.seat.PushPlayCardCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushReadyNextCmd;
import com.simple.game.ddz.domain.cmd.push.seat.PushSurrenderCmd;
import com.simple.game.ddz.domain.cmd.rtn.seat.RtnDdzGameSeatCmd;
import com.simple.game.ddz.domain.dto.DdzDesk;
import com.simple.game.ddz.domain.dto.config.DdzDeskItem;
import com.simple.game.ddz.domain.dto.config.DdzGameItem;
import com.simple.game.ddz.domain.dto.constant.ddz.GameProgress;

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
	protected TableDesk buildDesk(){
		return new DdzDesk(this);
	}
	
	/***
	 * 当前轮结束，准备下一轮
	 * @param playerId
	 * @param position
	 */
	public void readyNext(long playerId, int position, OutParam<SeatPlayer> outParam) {
		this.operatorVerfy();
		Player player = playerMap.get(playerId);
		if(player == null) {
			throw new BizException(String.format("%s不在游戏中", playerId));
		}
		
		preReadyNext(player);
		PushReadyNextCmd result = getDdzDesk().readyNext(playerId, position, outParam);
		this.broadcast(result, playerId);
		logger.info("{}已准备进行新一轮了,所在席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), tableDesk.getAddrNo(), position);
	}
	protected void preReadyNext(Player player) {
		//判断游戏币够不够
		if(player.getBcoin() < this.getDdzDeskItem().getMinReadyCoin()) {
			throw new BizException(String.format("%s的钱不够%s,无法进入下一局", player.getId(), this.getDdzDeskItem().getMinReadyCoin()));
		}
	}
	
	@Override
	protected boolean onScan() {
		return this.getDdzDesk().onScan();
	}
	
	@Override
	protected RtnGameInfoCmd getGameInfo() {
		RtnGameInfoCmd gameInfo = super.getGameInfo();
		return getDdzDesk().getGameInfo(gameInfo);
	}
	
	/***
	 * 选择某个席位座下
	 * 
	 * @param player
	 * @param position
	 */
	@Override
	public RtnGameSeatInfoCmd sitdown(long playerId, int position, OutParam<Player> outParam) {
		RtnGameSeatInfoCmd parent = super.sitdown(playerId, position, outParam);
		return getDdzDesk().getSeatInfo(parent);
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
		logger.info("{}抢地主,所在席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), tableDesk.getAddrNo(), position);
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
		logger.info("{}出牌,所在席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), tableDesk.getAddrNo(), position);
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
		logger.info("{}投降认输,所在席位:{}--{}--{}", outParam.getParam().getPlayer().getNickname(), gameItem.getName(), tableDesk.getAddrNo(), outParam.getParam().getGameSeat().getPosition());
	}
	
	public GameProgress getCurrentProgress() {
		return this.getDdzDesk().getCurrentProgress();
	}
	
	protected DdzDesk getDdzDesk() {
		return (DdzDesk)this.tableDesk;
	}
	protected DdzDeskItem getDdzDeskItem() {
		return (DdzDeskItem)this.deskItem;
	}
	
}
