package com.simple.game.core.domain.cmd.req;

import com.simple.game.core.domain.cmd.Cmd;
import com.simple.game.core.domain.cmd.rtn.RtnCommonCmd;

public abstract class ReqCmd extends Cmd{
	public void checkParam() {};
	
	public RtnCommonCmd valueOfRtnCommonCmd() {
		RtnCommonCmd rtmCmd = new RtnCommonCmd();
		rtmCmd.setCmd(getCmd());
		rtmCmd.setMessage("ok");
		return rtmCmd;
	}
}
