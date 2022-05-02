package com.simple.game.core.domain.cmd.req.game;

import com.simple.game.core.domain.cmd.req.ReqCmd;

import lombok.Data;

@Data
public abstract class ReqGameCmd extends ReqCmd{
	protected int playKind;
	protected int deskNo;
}
