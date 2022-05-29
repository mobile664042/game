package com.simple.game.core.domain.cmd.rtn;

import lombok.Data;
import lombok.ToString;

/***
 * 公共的返回信息
 * 
 * code对应请求值
 * 
 * @author zhibozhang
 *
 */
@Data
@ToString
public class RtnCommonCmd extends RtnCmd{
	protected int cmd;
	
	
	@Override
	public String toLogStr() {
		// TODO Auto-generated method stub
		return null;
	}
}
