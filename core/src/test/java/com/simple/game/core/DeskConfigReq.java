package com.simple.game.core;

import com.simple.game.core.domain.dto.config.DeskItem;

import lombok.Getter;

@Getter
public class DeskConfigReq {
	private int kind;
	private DeskItem deskItem;
	public DeskConfigReq(int kind, DeskItem deskItem) {
		this.kind = kind;
		this.deskItem = deskItem;
	}
	
}
