package com.simple.game.server.service;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simple.game.core.domain.cmd.req.game.ReqAdminPauseCmd;
import com.simple.game.core.domain.cmd.req.game.ReqAdminResumeCmd;
import com.simple.game.core.domain.dto.config.DeskItem;
import com.simple.game.ddz.domain.service.DdzAdminService;
import com.simple.game.server.cmd.req.desk.BuildReq;
import com.simple.game.server.cmd.req.desk.DestroyReq;
import com.simple.game.server.cmd.req.desk.PauseReq;
import com.simple.game.server.cmd.req.desk.ResumeReq;
import com.simple.game.server.cmd.rtn.game.KindRtn;

//@Slf4j
@Service
public class MyDdzGameService{
	
	@Autowired
	private DdzAdminService adminService;
	
	

	public List<KindRtn> getKindList() {
		List<DeskItem> deskItemList = adminService.getGameManager().getDeskItemList();
		List<KindRtn> list = new ArrayList<KindRtn>(deskItemList.size()); 
		for(DeskItem item : deskItemList) {
			KindRtn rtn = KindRtn.valueOfUser(item);
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
	
	
}
