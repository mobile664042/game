package com.simple.game.server.repository;
import org.springframework.stereotype.Repository;

import com.simple.game.server.dbEntity.User;

@Repository
public class UserCacheDao extends BaseCacheDao<Long, User> {


	public User getByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public long insert(User user) {
		// TODO Auto-generated method stub
		return 0;
	}

	public User getById(Long param) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
