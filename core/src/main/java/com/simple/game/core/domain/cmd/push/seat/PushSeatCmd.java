package com.simple.game.core.domain.cmd.push.seat;

import com.simple.game.core.domain.cmd.push.game.PushGameCmd;

import lombok.Data;

@Data
public abstract class PushSeatCmd extends PushGameCmd{
	protected int position;
}
