package com.simple.game.core.domain.good;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.push.PushChatCmd;
import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.push.PushDisconnectCmd;
import com.simple.game.core.domain.cmd.push.PushLeftCmd;
import com.simple.game.core.domain.cmd.rtn.RtnGameInfoCmd;
import com.simple.game.core.domain.cmd.rtn.RtnOnlineListCmd;
import com.simple.game.core.domain.dto.AddressNo;
import com.simple.game.core.domain.dto.BaseDesk;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.config.DeskItem;
import com.simple.game.core.domain.dto.config.GameItem;
import com.simple.game.core.domain.dto.constant.GameStatus;
import com.simple.game.core.domain.ext.Chat;
import com.simple.game.core.exception.BizException;

import lombok.Getter;
import lombok.ToString;

/***
 * 游戏
 * 
 * 游戏中最基础的组件部分
 * 
 * @author zhibozhang
 *
 */
@Getter
@ToString
public abstract class BaseGame implements AddressNo{
	private static Logger logger = LoggerFactory.getLogger(BaseGame.class);
	private long lastLogTime = System.currentTimeMillis();
	
	/***所在的游戏桌****/
	protected BaseDesk baseDesk;
	protected DeskItem deskItem;
	
	/***游戏配置****/
	protected GameItem gameItem;
	
	
	/***游戏状态****/
	protected GameStatus gameStatus;
	
	/***暂停结束时间****/
	private long pauseEndTime;
	
	public int getAddrNo() {
		return this.gameItem.getNo();
	}
	
	/***游戏初使化****/
	public final synchronized void init(GameItem gameItem, DeskItem deskItem){
		//状态判断
		if(gameStatus != null) {
			throw new BizException("游戏已经初使化过了！！！");
		}
		logger.info("游戏准备初使化");
		long startTime = System.currentTimeMillis();
		preInit(gameItem, deskItem);
		gameStatus = GameStatus.ready;
		this.gameItem = gameItem;
		this.deskItem = deskItem;
		this.baseDesk = buildDesk();
		logger.info("游戏初使化完成, 耗时:{}", (System.currentTimeMillis() - startTime));
	}
	protected void preInit(GameItem gameItem, DeskItem deskItem) {};
	
	protected BaseDesk buildDesk(){
		return new BaseDesk(this);
	}
	
	/***游戏运行(每隔300毫秒中扫描一次，推动游戏一直运行)****/
	public final synchronized void scan() {
		if(gameStatus == null || gameStatus == GameStatus.finished) {
			return;
		}
		
		if(this.getPauseTime() > 0) {
			//游戏暂停中
			return ;
		}
		
		gameStatus = GameStatus.ready;
		long startTime = System.currentTimeMillis();
		try {
			boolean result = onScan();
			long time = System.currentTimeMillis() - startTime;
			if(result || (System.currentTimeMillis()-lastLogTime) > 600000 || time > 2000) {
				logger.info("游戏进行一次计算, 耗时:{}", time);
				lastLogTime = System.currentTimeMillis();
			}
		}
		catch(Exception e) {
			logger.error("游戏计算异场", e);
		}
	}
	/***游戏运行(每隔300毫秒中扫描一次，推动游戏一直运行)(需要打印日志时返回true或有变化时返回true)****/
	protected abstract boolean onScan();
	
	/***中止游戏,强制结算,强退所有人员，销毁游戏****/
	public final synchronized void destroy() {
		if(gameStatus == null) {
			throw new BizException("游戏还未初使化，不可销毁！！！");
		}
		if(gameStatus == GameStatus.finished) {
			return;
		}
		
		logger.info("游戏准备销毁");
		long startTime = System.currentTimeMillis();
		onDestroy();
		logger.info("游戏销毁完成, 耗时:{}", (System.currentTimeMillis() - startTime));
	}
	protected void onDestroy() {};
	
	/***游戏暂停****/
	public final synchronized void pause(int seconds) {
		pauseEndTime = System.currentTimeMillis() + seconds * 1000; 
		logger.info("游戏准备暂停{}秒", seconds);
	}
	/***游戏取消暂停(恢复正常)****/
	public final synchronized void resume() {
		pauseEndTime = 0; 
		logger.info("游戏恢复正常");
	}
	
	/***游戏暂停多久时间(毫秒)(小于等于0表示游戏没有暂停)****/
	public final long getPauseTime() {
		if(pauseEndTime == 0) {
			return 0;
		}
		long time = System.currentTimeMillis() - pauseEndTime;
		if(time < 0) {
			return 0;
		}
		return time;
	}
	/***操作验证***/
	protected final void operatorVerfy() {
		if(gameStatus == null) {
			throw new BizException("游戏还未初使化，不可进入！！！");
		}
		if(gameStatus == GameStatus.finished) {
			throw new BizException("游戏已准备销毁，不可进入！！！");
		}
	}
	
	/***
	 * 进入游戏
	 */
	public final RtnGameInfoCmd join(Player player) {
		this.operatorVerfy();
		
		//是否有进入的限制条件
		this.preJoin(player);
		RtnGameInfoCmd rtnCmd = this.baseDesk.join(player);
		//广播进入信息
		this.broadcast(player.toPushJoinedCmd(), player.getId());
		logger.info("{}进入{}游戏:当前游戏桌{}", player.getNickname(), gameItem.getName(), deskItem.getNumber());
		return rtnCmd;
	}
	protected void preJoin(Player player) {};
	
	
	/***
	 * 获取游戏在线玩家
	 */
	public final RtnOnlineListCmd getRtnOnlineListCmd() {
		return this.baseDesk.getRtnOnlineListCmd();
	}
	
	/***
	 * 发送游戏广播
	 * @param cmd
	 * @param excludeIds
	 */
	public final void broadcast(PushCmd cmd, long ...excludeIds){
		this.operatorVerfy();
		this.baseDesk.broadcast(cmd, excludeIds);
	}
	
	/***
	 * 离开游戏
	 */
	public final void left(long playerId) {
		this.operatorVerfy();
		this.preLeft(playerId);
		OutParam<Player> outParam = OutParam.build();
		PushLeftCmd pushCmd = this.baseDesk.left(playerId, outParam);
		//广播离开信息
		this.broadcast(pushCmd, playerId);
		logger.info("{}离开{}游戏:当前游戏桌{}", outParam.getParam().getNickname(), gameItem.getName(), deskItem.getNumber());
	}
	protected void preLeft(long playerId) {}
	
	
	/***聊天****/
	public final void chat(long playerId, Chat message) {
		this.operatorVerfy();
		OutParam<Player> outParam = OutParam.build();
		PushChatCmd result = this.baseDesk.chat(playerId, message, outParam);
		this.broadcast(result, playerId);
		logger.info("{}在游戏桌:{}--{},发送:{}聊天", outParam.getParam().getNickname(), gameItem.getName(), deskItem.getNumber(), message.getKind());
	}

	/***系统强制踢人***/
	public final void kickout(long playerId) {
		this.operatorVerfy();
		this.preLeft(playerId);
		OutParam<Player> outParam = OutParam.build();
		PushLeftCmd pushCmd = this.baseDesk.left(playerId, outParam);
		this.broadcast(pushCmd, playerId);
		logger.info("强制在游戏桌:{}--{},将{}踢走", gameItem.getName(), deskItem.getNumber(), outParam.getParam().getNickname());
	}
	
	/***断网，掉线***/
	public final void disconnect(long playerId) {
		this.operatorVerfy();
		OutParam<Player> outParam = OutParam.build();
		PushDisconnectCmd pushCmd = this.baseDesk.disconnect(playerId, outParam);
		this.broadcast(pushCmd, playerId);
		logger.info("游戏桌:{}--{}的{}玩家掉线", gameItem.getName(), deskItem.getNumber(), outParam.getParam().getNickname());
	}
	
	/***掉线重连***/
	public final void connected(long playerId) {
		this.operatorVerfy();
		OutParam<Player> outParam = OutParam.build();
		PushDisconnectCmd pushCmd = this.baseDesk.disconnect(playerId, outParam);
		this.broadcast(pushCmd, playerId);
		logger.info("游戏桌:{}--{}的{}玩家掉线", gameItem.getName(), deskItem.getNumber(), outParam.getParam().getNickname());
	}
}
