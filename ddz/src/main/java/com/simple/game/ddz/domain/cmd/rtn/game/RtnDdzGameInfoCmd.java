package com.simple.game.ddz.domain.cmd.rtn.game;


import java.util.List;

import org.springframework.beans.BeanUtils;

import com.simple.game.core.domain.cmd.rtn.game.RtnGameInfoCmd;
import com.simple.game.ddz.domain.dto.constant.ddz.GameProgress;

import lombok.Data;

@Data
public class RtnDdzGameInfoCmd extends RtnGameInfoCmd{

	/***是否在进行中****/
	protected GameProgress currentProgress;
	
	/***投降位***/
	protected Integer surrenderPosition;
	
	/****公共牌(每一轮完成之前，保持不变)***/
	protected List<Integer> commonCards;
	
	/***地主位***/
	protected Integer landlordPosition;
	
	/***当前位位置***/
	protected Integer currentPosition;
	
	/***上一轮出的牌(需要对抗的牌)***/
	protected List<OutCard> battlefield;
	
	/***总共翻倍几次***/
	protected Integer doubleFinal;
	
	/***地主出几次牌***/
	protected Integer landlordPlayCardCount;
	
	/***农夫出了几次牌***/
	protected Integer farmerPlayCardCount;
	
	/***一次出牌***/
	@Data
	public static class OutCard {
		int position;
		List<Integer> cards;
	}
	
	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}

	public static RtnDdzGameInfoCmd copy(RtnGameInfoCmd gameInfo) {
		RtnDdzGameInfoCmd ddzGameInfo = new RtnDdzGameInfoCmd();
		BeanUtils.copyProperties(gameInfo, ddzGameInfo);
		return ddzGameInfo;
	}
}
