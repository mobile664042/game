package com.simple.game.server.service;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simple.game.core.domain.dto.config.DeskItem;
import com.simple.game.ddz.domain.manager.DdzGameManager;
import com.simple.game.server.cmd.rtn.game.KindRtn;

//@Slf4j
@Service
public class MyDdzGameService{
	@Autowired
	private DdzGameManager gameManager;
	
	

	public List<KindRtn> getKindList() {
		List<DeskItem> deskItemList = gameManager.getDeskItemList();
		List<KindRtn> list = new ArrayList<KindRtn>(deskItemList.size()); 
		for(DeskItem item : deskItemList) {
			KindRtn rtn = KindRtn.valueOfUser(item);
			list.add(rtn);
		}
		return list;
	}
	
	
}
