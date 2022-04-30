package com.simple.game.core.domain.cmd;

public abstract class Cmd {
	public abstract int getCode();
	public abstract String toLogStr();
}
