package com.simple.game.ddz.domain.dto;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.rtn.seat.RtnGameSeatInfoCmd;
import com.simple.game.core.domain.dto.GameSeat;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.exception.BizException;
import com.simple.game.ddz.domain.cmd.push.seat.PushReadyNextCmd;
import com.simple.game.ddz.domain.cmd.rtn.seat.RtnDdzGameSeatCmd;
import com.simple.game.ddz.domain.dto.config.DdzDeskItem;

import lombok.Getter;

/***
 * 游戏桌
 * 
 * @author zhibozhang
 *
 */
@Getter
public class DdzGameSeat extends GameSeat{ 
	private static Logger logger = LoggerFactory.getLogger(DdzGameSeat.class);

	public DdzGameSeat(DdzDesk desk, int position) {
		super(desk, position);
	}
	
	public DdzGameSeat(GameSeat gameSeat) {
		super(gameSeat.getDesk(), gameSeat.getPosition());
	}

	/***
	 * 是否准备了好没有
	 */
	private boolean ready = true;
	
	/***当前轮的跳过次数****/
	private int skipCount;
	
	/***当前轮的跳过次数****/
	private int timeoutCount;
	
	public void skipCountIncrease() {
		skipCount ++;
	}
	public void timeoutCountIncrease() {
		skipCount ++;
		timeoutCount ++;
	}
	
	/***
	 * 主席位想逃跑
	 */
	protected void doStandUpMaster() {
		if(!((DdzDesk)this.getDesk()).canStandUpMaster()) {
			throw new BizException("游戏正在进行中，主席位不可以站起(离开)");
		}
	}

	@Override
	protected void preSitdown(Player player) {
		//判断游戏币够不够
		if(player.getBcoin() < ((DdzDeskItem)this.desk.getCurrentGame().getDeskItem()).getMinSitdownCoin()) {
			throw new BizException(String.format("%s的钱不够%s,无法坐下主席位", player.getId(), ((DdzDeskItem)this.desk.getCurrentGame().getDeskItem()).getMinSitdownCoin()));
		}
	}
	@Override
	protected void doSitdownMaster() {
		this.ready = true;
		logger.info("{}已自动准备好了,所在席位:{}--{}--{}", master.get().getPlayer().getNickname(), this.desk.getCurrentGame().getGameItem().getName(), this.desk.getAddrNo(), this.getPosition());
	}
	
	@Override
	public RtnGameSeatInfoCmd getGameSeatInfo() {
		RtnDdzGameSeatCmd rtnCmd = new RtnDdzGameSeatCmd();
		rtnCmd.copy(super.getGameSeatInfo());
		rtnCmd.setReady(ready);
		rtnCmd.setSkipCount(skipCount);
		rtnCmd.setTimeoutCount(timeoutCount);
		return rtnCmd;
	}
	
	/***
	 * 是否掉线出牌
	 * @return
	 */
	public boolean isDiconnectPlayCard() {
		if(this.master.get() == null) {
			logger.warn("判断是否掉线时，主席位为空，是否有bug!");
			return false;
		}
		
		if(!this.master.get().getPlayer().getOnline().isOnline() && assistantMap.size() == 0) {
			return true;
		}
		return false;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	public PushReadyNextCmd readyNext() {
		//判断游戏币够不够
		if(master.get().getPlayer().getBcoin() < ((DdzDeskItem)this.desk.getCurrentGame().getDeskItem()).getMinReadyCoin()) {
			throw new BizException(String.format("%s的钱不够%s,无法准备下一轮", master.get().getPlayer().getId(), ((DdzDeskItem)this.desk.getCurrentGame().getDeskItem()).getMinReadyCoin()));
		}
		this.ready = true;
		return toPushReadyNextCmd();
	}
	
	public PushReadyNextCmd toPushReadyNextCmd() {
		return new PushReadyNextCmd();
	}
}
