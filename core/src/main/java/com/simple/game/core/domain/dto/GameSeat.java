package com.simple.game.core.domain.dto;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.push.PushCmd;
import com.simple.game.core.domain.cmd.rtn.RtnGameSeatCmd;
import com.simple.game.core.util.SimpleUtil;

import lombok.Data;
import lombok.ToString;

/***
 * 游戏席位
 * 
 * @author zhibozhang
 *
 */
@Data
@ToString
public class GameSeat implements AddressNo{
	private static Logger logger = LoggerFactory.getLogger(GameSeat.class);

	/***所属的桌子****/
	protected BaseDesk desk;
	
	/***席位号从1号开始****/
	protected int position;
	
//	/***扩展属性****/
//	private Object extConfig;
	
	/***席位主要人员****/
	private SeatPlayer master;
	
	/***一个席位可以有多个助手人员****/
	private final List<SeatPlayer> assistantList = new ArrayList<SeatPlayer>();
	
	/***一个席位可以有多个旁观人员****/
	private final List<SeatPlayer> onlookerList = new ArrayList<SeatPlayer>();
	
	private boolean stopOnlooker = false;
	private boolean stopAssistant = false;
	
	/***是否审核通过直播***/
	private boolean broadcasting = false;
	
	/***是否申请直播***/
	private boolean applyBroadcasted = false;

	public GameSeat(BaseDesk desk, int position) {
		this.desk = desk;
		this.position = position;
	}
	
	public int getAddrNo() {
		return position;
	}
	
	public final void broadcast(PushCmd cmd, long ...excludeIds){
		logger.info("cmd={}, 接收到推送信息！", cmd.toLogStr());
		long startTime = System.currentTimeMillis();
		if(master != null && !SimpleUtil.contain(master.getPlayer().getId(), excludeIds)) {
			master.getPlayer().getOnline().push(cmd);
		}
		for(SeatPlayer player : assistantList) {
			if(!SimpleUtil.contain(player.getPlayer().getId(), excludeIds)) {
				player.getPlayer().getOnline().push(cmd);
			}
		}
		for(SeatPlayer player : onlookerList) {
			if(!SimpleUtil.contain(player.getPlayer().getId(), excludeIds)) {
				player.getPlayer().getOnline().push(cmd);
			}
		}
		logger.info("cmd={}, 推送完成, 耗时:{}毫秒！", cmd.toLogStr(), System.currentTimeMillis() - startTime);
	}
	
	public RtnGameSeatCmd getRtnGameSeatCmd() {
		//TODO 
		return new RtnGameSeatCmd();
	}
}
