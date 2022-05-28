package com.simple.game.server.service;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simple.game.core.domain.cmd.req.game.ReqAdminPauseCmd;
import com.simple.game.core.domain.cmd.req.game.ReqAdminResumeCmd;
import com.simple.game.core.domain.dto.TableDesk;
import com.simple.game.core.domain.dto.config.DeskItem;
import com.simple.game.ddz.domain.dto.DdzDesk;
import com.simple.game.ddz.domain.service.DdzAdminService;
import com.simple.game.server.cmd.req.desk.BuildReq;
import com.simple.game.server.cmd.req.desk.DestroyReq;
import com.simple.game.server.cmd.req.desk.PauseReq;
import com.simple.game.server.cmd.req.desk.ResumeReq;
import com.simple.game.server.cmd.rtn.game.DdzDeskRtn;
import com.simple.game.server.cmd.rtn.game.DdzKindRtn;

@Service
public class MyDdzGameService{
	
	@Autowired
	private DdzAdminService adminService;
	
	

	public List<DdzKindRtn> getKindList() {
		List<DeskItem> deskItemList = adminService.getGameManager().getDeskItemList();
		List<DdzKindRtn> list = new ArrayList<DdzKindRtn>(deskItemList.size()); 
		for(DeskItem item : deskItemList) {
			DdzKindRtn rtn = DdzKindRtn.valueOfUser(item);
			list.add(rtn);
		}
		return list;
	}



	public void buildDdzDesk(BuildReq req) {
		adminService.buildGameDesk(req.getPlayKind(), req.getCount());
	}



	public void destroyDesk(DestroyReq req) {
		adminService.destroy(req.getPlayKind(), req.getDeskNo());
	}



	public void resumeDdzDesk(ResumeReq req) {
		ReqAdminResumeCmd reqCmd = new ReqAdminResumeCmd();
		reqCmd.setPlayKind(req.getPlayKind());
		reqCmd.setDeskNo(req.getDeskNo());
		adminService.resume(reqCmd);		
	}



	public void pauseDdzDesk(PauseReq req) {
		ReqAdminPauseCmd reqCmd = new ReqAdminPauseCmd();
		reqCmd.setPlayKind(req.getPlayKind());
		reqCmd.setDeskNo(req.getDeskNo());
		reqCmd.setSeconds(req.getSecond());
		adminService.pause(reqCmd);
	}



	public List<DdzDeskRtn> getDeskList(int playKind) {
		List<TableDesk> list = adminService.getGameManager().getTableDeskList(playKind);
		if(list == null) {
			return null;
		}
		
		List<DdzDeskRtn> result = new ArrayList<DdzDeskRtn>();
		for(TableDesk tableDesk : list) {
			DdzDesk ddzDesk = (DdzDesk)tableDesk;
			
			DdzDeskRtn desk = new DdzDeskRtn();
			desk.setDeskNo(tableDesk.getDeskNo());
			desk.setGameStatus(ddzDesk.getGameStatus());
			desk.setOnlineCount(tableDesk.getOnlineCount());
			desk.setCurrentProgress(ddzDesk.getCurrentProgress());
			desk.setPauseTime((int)tableDesk.getPauseTime());
			result.add(desk);
		}
		
		return result;
	}
	
	
}
