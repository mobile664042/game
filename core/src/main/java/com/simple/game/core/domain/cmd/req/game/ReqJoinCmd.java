package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.push.game.PushJoinCmd;
import com.simple.game.core.util.GameSession;

import lombok.Data;

@Data
public class ReqJoinCmd extends ReqGameCmd{
	public final static int CMD = 101003;
	
	private String nickname;
    private int sex;
    private String telphone;
    private int headPic;
    /**带入币(桌面币，不一定等于bankCoin, 一般使用这个做游戏计算，变动次数多)***/
	private long bcoin;
	private GameSession session;
	
	@Override
	public int getCmd() {
		return CMD;
	}

	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public PushJoinCmd valueOfPushJoinCmd() {
		PushJoinCmd pushCmd = new PushJoinCmd();
		pushCmd.setDeskNo(deskNo);
		pushCmd.setPlayKind(playKind);
		return pushCmd;
	}
	
}
