package com.simple.game.core.domain.dto.seat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.push.game.notify.PushNotifyApplyManagerCmd;
import com.simple.game.core.domain.cmd.push.seat.PushApplyBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.push.seat.PushCancleBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqApplyBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqBroadcastLiveCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqCancleBroadcastLiveCmd;
import com.simple.game.core.domain.dto.GameSeat;
import com.simple.game.core.domain.dto.GameSessionInfo;
import com.simple.game.core.domain.dto.Player;
import com.simple.game.core.domain.dto.SeatPlayer;
import com.simple.game.core.domain.dto.SeatPlugin;
import com.simple.game.core.domain.dto.constant.SeatPost;
import com.simple.game.core.exception.BizException;

/***
 * 直播
 * @author Administrator
 *
 */
public class BroadcastLivePlugin extends SeatPlugin{
	public static final String PLUGIN_NAME = "BroadcastLive";
	private final static Logger logger = LoggerFactory.getLogger(BroadcastLivePlugin.class);
	
	public BroadcastLivePlugin(GameSeat gameSeat) {
		super(gameSeat, null);
	}
	
	

	/***申请直播****/
	public void applyBroadcastLive(GameSessionInfo gameSessionInfo, ReqApplyBroadcastLiveCmd reqCmd) {
		gameSeat.getDesk().operatorVerfy();
		if(gameSeat.getMaster().get() == null) {
			throw new BizException(String.format("没有管理员，不可以同意申请直播"));
		}
		Player player = gameSeat.getDesk().getPlayer(gameSessionInfo.getPlayerId());
		
		SeatPlayer seatPlayer = gameSeat.getSeatPlayer(player.getId());
		if(seatPlayer == null) {
			throw new BizException(String.format("%s不在席位上，不可以同意申请直播", player.getId()));
		}
		if(seatPlayer.getSeatPost() == SeatPost.onlooker) {
			throw new BizException(String.format("%s旁观人员，不可以同意申请直播", player.getId()));
		}
		
		if(seatPlayer.getGameSeat().isBroadcasting()) {
			throw new BizException(String.format("该主席位已经是直播了，不可以再申请直播！！"));
		}
		
		if(gameSeat.getDesk().getManagerId() == player.getId()) {
			gameSeat.setBroadcasting(true);
			gameSeat.setApplyBroadcasted(false) ;
			PushApplyBroadcastLiveCmd pushCmd = reqCmd.valueOfPushApplyBroadcastLiveCmd();
			pushCmd.setPosition(gameSeat.getPosition());
			pushCmd.setPlayerId(gameSessionInfo.getPlayerId());
			pushCmd.setNickname(player.getNickname());
			pushCmd.setHeadPic(player.getHeadPic());
			gameSeat.getDesk().broadcast(pushCmd, gameSessionInfo.getPlayerId());
			return;
		}
		
		PushNotifyApplyManagerCmd pushCmd = new PushNotifyApplyManagerCmd();
		pushCmd.setPlayerId(player.getId());
		pushCmd.setNickname(player.getNickname());
		pushCmd.setHeadPic(player.getHeadPic());
		gameSeat.getDesk().getManager().getOnline().push(pushCmd);
		logger.info("{}向管理员{}发送主播申请", player.getNickname(), gameSeat.getDesk().getManager().getNickname());
	}
	
	
	/***取消直播****/
	public void cancleBroadcastLive(GameSessionInfo gameSessionInfo, ReqCancleBroadcastLiveCmd reqCmd) {
		SeatPlayer seatPlayer = gameSeat.getSeatPlayer(gameSessionInfo.getPlayerId());
		if(seatPlayer == null) {
			throw new BizException(String.format("%s不在席位上，不可以同意申请直播", gameSessionInfo.getPlayerId()));
		}
		if(seatPlayer.getSeatPost() == SeatPost.onlooker) {
			throw new BizException(String.format("%s旁观人员，不可以取消直播", gameSessionInfo.getPlayerId()));
		}
		
		if(!seatPlayer.getGameSeat().isBroadcasting()) {
			if(!seatPlayer.getGameSeat().isApplyBroadcasted()) {
				throw new BizException(String.format("该主席位不是直播，也没人申请直播！！"));
			}
		}
		
		Player player = seatPlayer.getPlayer();
		
		gameSeat.setBroadcasting(false);
		gameSeat.setApplyBroadcasted(false);
		logger.info("{}取消直播席位:{}", player.getNickname(), seatPlayer.getGameSeat().getAddrNo());
		PushCancleBroadcastLiveCmd pushCmd = reqCmd.valueOfPushCancleBroadcastLiveCmd();
		pushCmd.setPosition(gameSeat.getPosition());
		pushCmd.setPlayerId(gameSessionInfo.getPlayerId());
		pushCmd.setNickname(player.getNickname());
		pushCmd.setHeadPic(player.getHeadPic());
		
		//发送广播
		gameSeat.getDesk().broadcast(pushCmd, gameSessionInfo.getPlayerId());
	}
	/***直播****/
	public void broadcastLive(GameSessionInfo gameSessionInfo, ReqBroadcastLiveCmd reqCmd, byte[] data) {
		SeatPlayer seatPlayer = gameSeat.getSeatPlayer(gameSessionInfo.getPlayerId());
		if(seatPlayer == null) {
			throw new BizException(String.format("%s不在席位上，不可以同意申请直播", gameSessionInfo.getPlayerId()));
		}
		if(seatPlayer.getSeatPost() == SeatPost.onlooker) {
			throw new BizException(String.format("%s旁观人员，不可以取消直播", gameSessionInfo.getPlayerId()));
		}
		
		if(!seatPlayer.getGameSeat().isBroadcasting()) {
			if(!seatPlayer.getGameSeat().isApplyBroadcasted()) {
				throw new BizException(String.format("该主席位不是直播，也没人申请直播！！"));
			}
		}
		//TODO 
		Player player = seatPlayer.getPlayer();
		logger.info("{}正在直播:席位:{}", player.getNickname(), seatPlayer.getGameSeat().getAddrNo());
	}
	
	
	@Override
	public String getPluginName() {
		return PLUGIN_NAME;
	}


}
