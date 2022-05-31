package com.simple.game.ddz.robot;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.constant.GameConstant;
import com.simple.game.core.domain.cmd.req.game.ReqJoinCmd;
import com.simple.game.core.domain.cmd.req.seat.ReqSitdownCmd;
import com.simple.game.core.domain.dto.GameSeat;
import com.simple.game.core.domain.dto.GameSessionInfo;
import com.simple.game.core.domain.dto.SeatPlayer;
import com.simple.game.core.domain.dto.TableDesk;
import com.simple.game.core.robot.ActionListener;
import com.simple.game.core.robot.RobotGameSession;

public class DdzRobotListener/* extends RobotListener */{
	private final static Logger logger = LoggerFactory.getLogger(DdzRobotListener.class);
	private static final RobotScan robotThread = new RobotScan();
	private static final DdzRobotListener instance = new DdzRobotListener();
	protected static BlockingQueue<TableDesk> eventList = new LinkedBlockingQueue<TableDesk>(150);

	/****桌号位序***/
	protected final static AtomicInteger NUMBER_INDEX = new AtomicInteger(100000001); 
	/***
	 * value:desk.addrNo
	 */
	private final static HashSet<String> robotDeskSet = new HashSet<String>();
	
	static {
		Thread thread = new Thread(robotThread, "robotScan");
		thread.setDaemon(true);
		thread.start();
		logger.info("扫描机器人线程已启动!");
	}
	

	public static void submitEvent(SeatPlayer seatPlayer) {
		if(seatPlayer.getGameSeat().getDesk().getDeskItem().getRobotCount() > 0) {
			eventList.offer(seatPlayer.getGameSeat().getDesk());
		}
	}
	
	final static class RobotScan implements Runnable{
		@Override
		public void run() {
			while(true) {
				for(int i=0; i<10; i++) {
					TableDesk tableDesk;
					try {
						tableDesk = eventList.take();
						instance.onSitdown(tableDesk);
					} catch (InterruptedException e) {
						logger.warn("", e);
					}
				}
				logger.info("扫描一次坐下人员!");
				Random random = new Random();
				
				int time = 1000 + random.nextInt(2000);
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					logger.error("", e);
				}
			}
		}
	}
	
	protected void onSitdown(TableDesk tableDesk) {
		String key = tableDesk.getAddrNo();
		if(robotDeskSet.contains(key)) {
			return;
		}
		robotDeskSet.add(key);
		
		Random random = new Random();
		for(int i=0; i<tableDesk.getDeskItem().getRobotCount(); i++) {
			
			ActionListener actionListener = new DdzActionListener();
			RobotGameSession robotGameSession = new RobotGameSession(actionListener);
			long playerId = NUMBER_INDEX.getAndAdd(10);
			String nickName = BobotName.names[random.nextInt(BobotName.names.length)];
			int sex = (1 + random.nextInt(2));
			String telphone = "15"+ (100000000 + random.nextInt(800000000));
			int headpic = random.nextInt(10)+1;
			
			//建立连接
			robotGameSession.getAttachment().put(GameConstant.PLAYER_ID, playerId);
			robotGameSession.getAttachment().put(GameConstant.NICKNAME, nickName);
			robotGameSession.getAttachment().put(GameConstant.SEX, sex);
			robotGameSession.getAttachment().put(GameConstant.TELPHONE, telphone);
			robotGameSession.getAttachment().put(GameConstant.HEADPIC, headpic);
			
			GameSessionInfo gameSessionInfo = new GameSessionInfo();
	    	gameSessionInfo.setPlayerId(playerId);
	    	robotGameSession.getAttachment().put(GameConstant.GAME_SESSION_INFO, gameSessionInfo);
	    	RobotPlayer robotPlayer = new RobotPlayer();
	    	robotGameSession.getAttachment().put(RobotPlayer.ROBOT_CACHE, robotPlayer);
	    	try {
	    		{
	    			//准备进入游戏
		    		ReqJoinCmd reqCmd = new ReqJoinCmd();
		    		reqCmd.setPlayKind(i);
		    		reqCmd.setDeskNo(i);
		    		reqCmd.setBcoin(1000000000);
		    		tableDesk.join(reqCmd, robotGameSession);
		    		logger.info("机器人{}，进入游戏：", playerId, tableDesk.getAddrNo());
		    	}
	    		{
	    			//准备坐下
	    			GameSeat idleSeat = tableDesk.getIdelGameSeat();
	    			if(idleSeat != null) {
	    				ReqSitdownCmd reqCmd = new ReqSitdownCmd();
	    				reqCmd.setPosition(idleSeat.getPosition());
	    				idleSeat.sitdown(gameSessionInfo, reqCmd);
	    				robotPlayer.setPosition(idleSeat.getPosition());
	    			}
	    		}
	    	}
	    	catch(Exception e) {
	    		logger.info("机器人{}，进入游戏失败", playerId, e);
	    	}
		}
	}
	
	
}
