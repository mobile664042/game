//package com.simple.game.core.robot;
//
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//
//import com.simple.game.core.domain.dto.GameSeat;
//import com.simple.game.core.domain.dto.SeatPlayer;
//
//public class RobotListener {
//	protected static BlockingQueue<GameSeat> eventList = new LinkedBlockingQueue<GameSeat>(150);
//
//	public static void submitEvent(SeatPlayer seatPlayer) {
//		if(seatPlayer.getGameSeat().getDesk().getDeskItem().getRobotCount() > 0) {
//			eventList.offer(seatPlayer.getGameSeat());
//		}
//	}
//	
//}
