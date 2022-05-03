package com.simple.game.ddz.domain.manager;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.simple.game.core.domain.dto.config.DeskItem;
import com.simple.game.core.domain.dto.config.GameItem;
import com.simple.game.core.domain.good.BaseGame;
import com.simple.game.core.domain.manager.GameManager;
import com.simple.game.core.exception.BizException;
import com.simple.game.ddz.domain.dto.config.DdzDeskItem;
import com.simple.game.ddz.domain.dto.config.DdzGameItem;
import com.simple.game.ddz.domain.good.DdzGame;

import lombok.Getter;

/***
 * 斗地主的游戏管理
 * 
 * @author zhibozhang
 *
 */
@Getter
public class DdzGameManager extends GameManager {
	private DdzGameManager() {}
	private GameItem gameItem;
	private List<DeskItem> deskItemList;

	@Override
	public BaseGame newInstanceload(GameItem gameItem, DeskItem deskItem) {
		return new DdzGame((DdzGameItem)gameItem, (DdzDeskItem)deskItem);
	}
	
	public static DdzGameManager buildDefault() {
		DdzGameManager gameManager = new DdzGameManager();
		gameManager.gameItem = new DdzGameItem();
		gameManager.deskItemList = new ArrayList<DeskItem>(1);
		gameManager.deskItemList.add(new DdzDeskItem());
		return gameManager;
	}
	
	@SuppressWarnings("unchecked")
	public static DdzGameManager buildInstance(String gameConfigJson, String deskConfigJson) {
		DdzGameManager gameManager = new DdzGameManager();
		try {
			DdzGameItem gameItem = JSON.parseObject(gameConfigJson, DdzGameItem.class);
			@SuppressWarnings("rawtypes")
			List deskItemList = JSON.parseArray(deskConfigJson, DdzDeskItem.class);
			if(gameItem == null) {
				throw new IllegalArgumentException(gameConfigJson);
			}
			if(deskItemList == null || deskItemList.size()==0) {
				throw new IllegalArgumentException(deskConfigJson);
			}
			
			gameManager.gameItem = gameItem;
			gameManager.deskItemList = deskItemList;
		}
		catch(Exception e) {
			throw new BizException("斗地主游戏配置有误", e);
		}
		
		return gameManager;
	}



}
