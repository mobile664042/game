package com.simple.game.ddz.domain.service;



import com.simple.game.core.domain.service.admin.AdminService;
import com.simple.game.ddz.domain.manager.DdzGameManager;

import lombok.Getter;
import lombok.ToString;

/***
 * 管理部分
 * 
 * @author zhibozhang
 *
 */
@Getter
@ToString
public class DdzAdminService extends AdminService{
//	private final static Logger logger = LoggerFactory.getLogger(DdzAdminService.class);
	
	public DdzAdminService(DdzGameManager gameManager) {
		super(gameManager);
	}



	
	
}
