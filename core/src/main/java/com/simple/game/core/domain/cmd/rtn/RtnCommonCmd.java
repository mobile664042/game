package com.simple.game.core.domain.cmd.rtn;

import lombok.Data;

/***
 * 公共的返回信息
 * 
 * code对应请求值
 * 
 * @author Administrator
 *
 */
@Data
public class RtnCommonCmd extends RtnCmd{
	protected int code;
	
	
	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
}