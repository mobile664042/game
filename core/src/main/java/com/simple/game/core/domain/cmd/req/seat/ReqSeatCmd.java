package com.simple.game.core.domain.cmd.req.seat;

import com.simple.game.core.domain.cmd.req.game.ReqGameCmd;

import lombok.Data;

@Data
public abstract class ReqSeatCmd extends ReqGameCmd{
	protected int position;
}
