package com.simple.game.core.robot;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simple.game.core.domain.cmd.Cmd;
import com.simple.game.core.util.GameSession;
import com.simple.game.core.util.MyThreadFactory;

public class ActionListener {
	private final static Logger logger = LoggerFactory.getLogger(ActionListener.class);
	
	protected static final int coreSize = Runtime.getRuntime().availableProcessors();
	protected final static ThreadPoolExecutor pool = new ThreadPoolExecutor(1, coreSize,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(150000),
            new MyThreadFactory("robotAction"));
	
	public void submitEvent(GameSession gameSession, Cmd cmd) {
		try {
			Runnable task = new CmdTask(gameSession, cmd);
			pool.submit(task);
		}
		catch(Exception e) {
			logger.error("cmd={}, gameSession={} 接收事件失败！", cmd.toLogStr(), gameSession, e);
		}
	}
	
	public final class CmdTask implements Runnable{
		GameSession gameSession;
		Cmd cmd;
		public CmdTask(GameSession gameSession, Cmd cmd) {
			this.gameSession = gameSession;
			this.cmd = cmd;
		}
		@Override
		public void run() {
			onReceived(this);
		}
		public GameSession getGameSession() {
			return gameSession;
		}
		public Cmd getCmd() {
			return cmd;
		}
	}
	
	
	protected void onReceived(CmdTask cmdTask) {}
}
