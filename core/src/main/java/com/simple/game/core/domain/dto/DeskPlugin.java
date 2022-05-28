package com.simple.game.core.domain.dto;

public abstract class DeskPlugin {
	protected TableDesk tableDesk;
	/***最多只能有一个依赖****/
	protected DeskPlugin dependPlugin;
	
	public DeskPlugin(TableDesk tableDesk, DeskPlugin dependPlugin) {
		this.tableDesk = tableDesk;
		this.dependPlugin = dependPlugin;
		tableDesk.putPlugin(this);
	}
	
	public abstract String getPluginName();
	
	
}
