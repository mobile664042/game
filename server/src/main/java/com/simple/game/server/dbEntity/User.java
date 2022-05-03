package com.simple.game.server.dbEntity;


import java.io.Serializable;

import lombok.Data;

/***
 * 用户
 * 
 * @author Administrator
 *
 */
@Data
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
    /**
     * 用户id
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 用户名
     */
    private String username;

    /**
     * 性别
     */
    private int sex;
    
    /**
     * 手机号码
     */
    private String telphone;
    
    /**
     * 头像
     */
    private int headPic;
}