package com.simple.game.core.domain.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.OutParam;
import com.simple.game.core.domain.cmd.rtn.seat.RtnGameSeatInfoCmd;
import com.simple.game.core.domain.dto.constant.SeatPost;
import com.simple.game.core.domain.ext.Chat;
import com.simple.game.core.domain.ext.Gift;
import com.simple.game.core.domain.good.BaseGame;
import com.simple.game.core.domain.good.TableGame;
import com.simple.game.core.exception.BizException;

import lombok.ToString;

/***
 * 游戏桌
 * 
 * 游戏附加的一些核心玩法
 * 
 * @author zhibozhang
 *
 */
@ToString
public class TableDesk implements AddressNo{
	/****桌号位序***/
	private final static AtomicInteger NUMBER_INDEX = new AtomicInteger(101); 
	
	private final static Logger logger = LoggerFactory.getLogger(TableDesk.class);
	
	/***
	 * 正在进行席位
	 * key position,席位
	 */
	protected final ConcurrentHashMap<Integer, GameSeat> seatPlayingMap = new ConcurrentHashMap<Integer, GameSeat>();
	
	/***
	 * 桌号
	 */
	private final int deskNo;
	
	/***当前正在进行的游戏****/
	protected TableGame currentGame;
	
	
	public TableDesk(TableGame game) {
		this.currentGame = game;
		for(int position = currentGame.getDeskItem().getMinPosition(); position <= currentGame.getDeskItem().getMinPosition(); position++) {
			GameSeat gameSeat = buildGameSeat(position); 
			seatPlayingMap.put(position, gameSeat);
		}
		deskNo = NUMBER_INDEX.getAndIncrement();
	} 

	public String getAddrNo() {
		return "@" + currentGame.getDeskItem().getPlayKind()  + "@" +  deskNo;
	}
	
	
	/***
	 * 选择某个席位座下
	 * 
	 * @param player
	 * @param position
	 */
	public final RtnGameSeatInfoCmd sitdown(Player player, int position) {
		GameSeat gameSeat = getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		
		gameSeat.sitdown(player);
		
		logger.info("{}进入游戏:并在席位{}坐下", player.getNickname(), gameSeat.getAddrNo(), position);
		return gameSeat.getGameSeatInfo();
	}
	
	/***
	 * 获取桌位玩家
	 * 需要页面这个接口的访问频率
	 */
	public List<SeatPlayer> getSeatPlayerList(int position, int fromPage) {
		GameSeat gameSeat = getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		List<SeatPlayer> list = gameSeat.getSeatPlayerList();
		List<SeatPlayer> result = new ArrayList<SeatPlayer>();
		int fromIndex = fromPage * BaseGame.PAGE_SIZE;
		int toIndex = fromIndex + BaseGame.PAGE_SIZE;
		for(int i=fromIndex; i<list.size() && i<toIndex; i++) {
			result.add(list.get(i));
		}
		return result;
	}
	public List<SeatPlayer> getAssistantList(int position) {
		GameSeat gameSeat = getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		return gameSeat.getAssistantList();
	}
	
	/***
	 * 快速坐下
	 * 
	 * @param player
	 */
	public final RtnGameSeatInfoCmd quickSitdown(Player player) {
		GameSeat gameSeat = getIdelGameSeat();
		if(gameSeat == null) {
			throw new BizException(String.format("已经没有空闲的席位了"));
		}
		
		gameSeat.sitdown(player);
		logger.info("{}进入游戏: 抢到{}席位并坐下", player.getNickname(), gameSeat.getAddrNo());
		return gameSeat.getGameSeatInfo();
	}
	
	/***
	 * 申请辅助
	 * @param playerId
	 */
	public void applyAssistant(Player player, int position) {
		GameSeat gameSeat = getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("已经没有空闲的席位了"));
		}
		
		//是否有进入的限制条件
		gameSeat.applyAssistant(player);
		logger.info("{}在游戏席位: {}申请辅助", player.getNickname(), gameSeat.getAddrNo());
		//等待主席位应答
	}
	
	/***
	 * 同意XXX成为辅助
	 * @param player
	 * @param id
	 */
	public void approveApplyAssistant(Player master, int position, Player other, OutParam<Player> outParam) {
		GameSeat gameSeat = getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("已经没有空闲的席位了"));
		}
		
		gameSeat.approveApplyAssistant(master, other);
		outParam.setParam(other);
		logger.info("{}同意席位:{}的{}成为辅助", master.getNickname(), gameSeat.getAddrNo(), position, other.getNickname());
	}
	
	/***
	 * 从席位中站起来
	 * @param player
	 */
	public final void standUp(Player player, int position) {
		GameSeat gameSeat = getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		
		gameSeat.standUp(player);
		if(gameSeat.getFansCount() == 0) {
			this.removeGameSeat(position);
		}
		logger.info("{}在席位:{}站起来了", player.getNickname(), gameSeat.getAddrNo());
	}
	
	public void stopAssistant(Player player, int position) {
		GameSeat gameSeat = getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		gameSeat.stopAssistant(player);
		logger.info("{}在席位:{}停止申请助手", player.getNickname(), gameSeat.getAddrNo());
	}	
	
	public void stopOnlooker(Player player, int position) {
		GameSeat gameSeat = getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		gameSeat.stopOnlooker(player);
		logger.info("{}在席位:{}停止申请旁观者", player.getNickname(), gameSeat.getAddrNo());
	}
	public void bootAssistant(Player player, int position) {
		GameSeat gameSeat = getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		gameSeat.bootAssistant(player);
		logger.info("{}在席位:{}开启申请助手", player.getNickname(), gameSeat.getAddrNo());
	}
	public void bootOnlooker(Player player, int position) {
		GameSeat gameSeat = getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		gameSeat.bootOnlooker(player);
		logger.info("{}在席位:{}开启申请旁观者", player.getNickname(), gameSeat.getAddrNo());
	}

	public void applySeatSuccessor(Player player, int position) {
		GameSeat gameSeat = getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}		
		gameSeat.applySeatSuccessor(player);
		logger.info("{}在席位:{}申请主席位继任者", player.getNickname(), gameSeat.getAddrNo());
	}
	/***
	 * 设置(下一轮)主席位继任人
	 * @param player
	 */
	public final void setSeatSuccessor(Player master, int position, Player other, OutParam<Player> outParam) {
		GameSeat gameSeat = getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s无效的席位号", position));
		}
		
		gameSeat.setSeatSuccessor(master, other);
		outParam.setParam(other);
		logger.info("{}在席位:{}将主席位继任者传给{}成功", other.getNickname(), gameSeat.getAddrNo(), other.getNickname());
	}
	/***
	 * 强制席中的某人站起
	 * @param masterId
	 * @param playerId
	 */
	public void forceStandUp(Player master, int position, Player other) {
		GameSeat gameSeat = getGameSeat(position);
		if(gameSeat == null) {
			throw new BizException(String.format("已经没有空闲的席位了"));
		}
		
		gameSeat.forceStandUp(master, other);
		logger.info("{}在席位:{}请 {}站起", master.getNickname(), gameSeat.getAddrNo(), other.getNickname());
	}
	
	public void reward(Player player, List<Integer> positionList, Gift gift) {
		checkPosistion(positionList);
		
		//TODO 需判断扣款
		//SeatPlayer seatPlayer = getSeatPlayer(playerId);
	}
	/***
	 *聊天
	 */
	public void chat(Player player, List<Integer> positionList, Chat message) {
		checkPosistion(positionList);
		//TODO 需判断扣款
	}

	protected GameSeat getGameSeat(int position) {
		return seatPlayingMap.get(position);
	}
	protected GameSeat removeGameSeat(int position) {
		return seatPlayingMap.remove(position);
	}
	protected GameSeat getIdelGameSeat() {
		for(GameSeat gameSeat : seatPlayingMap.values()) {
			if(gameSeat.isIdle()) {
				return gameSeat;
			}
		}
		return null;
	}
	protected GameSeat buildGameSeat(int position){
		return new GameSeat(this, position);
	}
	


	public boolean applyBroadcastLive(Player player, OutParam<SeatPlayer> outParam) {
		SeatPlayer seatPlayer = this.getSeatPlayer(player.getId());
		if(seatPlayer == null) {
			throw new BizException(String.format("%s不在席位上，不可以同意申请直播", player.getId()));
		}
		if(seatPlayer.getSeatPost() == SeatPost.onlooker) {
			throw new BizException(String.format("%s旁观人员，不可以同意申请直播", player.getId()));
		}
		
		if(seatPlayer.getGameSeat().isBroadcasting()) {
			throw new BizException(String.format("该主席位已经是直播了，不可以再申请直播！！"));
		}
		
		outParam.setParam(seatPlayer);
		if(this.getTableGame().getManagerId() == player.getId()) {
			seatPlayer.getGameSeat().broadcasting = true;
			seatPlayer.getGameSeat().applyBroadcasted = false ;
			return true;
		}
		return false;
	}
	
	public void cancelBroadcastLive(Player player, OutParam<SeatPlayer> outParam) {
		SeatPlayer seatPlayer = this.getSeatPlayer(player.getId());
		if(seatPlayer == null) {
			throw new BizException(String.format("%s不在席位上，不可以同意申请直播", player.getId()));
		}
		if(seatPlayer.getSeatPost() == SeatPost.onlooker) {
			throw new BizException(String.format("%s旁观人员，不可以取消直播", player.getId()));
		}
		
		if(!seatPlayer.getGameSeat().isBroadcasting()) {
			if(!seatPlayer.getGameSeat().isApplyBroadcasted()) {
				throw new BizException(String.format("该主席位不是直播，也没人申请直播！！"));
			}
		}
		
		seatPlayer.getGameSeat().broadcasting = false;
		seatPlayer.getGameSeat().applyBroadcasted = false;
		outParam.setParam(seatPlayer);
		logger.info("{}取消直播席位:{}", player.getNickname(), seatPlayer.getGameSeat().getAddrNo());
	}
	
	public void approveBroadcastLive(int position) {
		GameSeat gameSeat = this.seatPlayingMap.get(position);
		if(gameSeat == null) {
			throw new BizException(String.format("%s席位没有人入座", position));
		}
		if(gameSeat.isBroadcasting()) {
			throw new BizException(String.format("%s席位已经是可以直播的啦", position));
		}
		if(!gameSeat.isApplyBroadcasted()) {
			throw new BizException(String.format("%s席位没有人申请直播", position));
		}
		gameSeat.broadcasting = true;
		gameSeat.applyBroadcasted = false;
	}

	public void broadcastLive(Player player, OutParam<SeatPlayer> outParam) {
		SeatPlayer seatPlayer = this.getSeatPlayer(player.getId());
		if(seatPlayer == null) {
			throw new BizException(String.format("%s不在席位上，不可以直播", player.getId()));
		}
		if(seatPlayer.getSeatPost() != SeatPost.master) {
			throw new BizException(String.format("%s不是主席位，不可以直播", player.getId()));
		}
		outParam.setParam(seatPlayer);
		
		logger.info("{}正在直播:席位:{}", player.getNickname(), seatPlayer.getGameSeat().getAddrNo());
	}
	
	protected void checkPosistion(List<Integer> positionList) throws BizException{
		for(int position : positionList) {
			if(this.seatPlayingMap.containsKey(position)) {
				throw new BizException(String.format("%s席位没有人入座", position));
			}
		}
	}

	protected SeatPlayer getSeatPlayer(long playerId) {
		for(GameSeat gameSeat : this.seatPlayingMap.values()) {
			SeatPlayer seatPlayer = gameSeat.getSeatPlayer(playerId);
			if(seatPlayer != null) {
				return seatPlayer;
			}
		}
		return null;
	}

	public TableGame getTableGame() {
		return (TableGame)this.currentGame;
	}

	public int getDeskNo() {
		return deskNo;
	}

	
}
