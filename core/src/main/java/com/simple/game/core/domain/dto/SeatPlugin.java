package com.simple.game.core.domain.dto;

public abstract class SeatPlugin {
	protected GameSeat gameSeat;
	/***最多只能有一个依赖****/
	protected SeatPlugin dependPlugin;
	
	public SeatPlugin(GameSeat gameSeat, SeatPlugin dependPlugin) {
		this.gameSeat = gameSeat;
		this.dependPlugin = dependPlugin;
		gameSeat.putPlugin(this);
	}
	
	public abstract String getPluginName();
	
	
}
