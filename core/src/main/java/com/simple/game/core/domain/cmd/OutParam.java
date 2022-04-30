package com.simple.game.core.domain.cmd;

import lombok.Data;

/***
 * 输出参数
 * @author zhibozhang
 *
 * @param <T>
 */
@Data
public class OutParam<T> {
	T param;
	
	public static <T> OutParam<T> build() {
		return new OutParam<T>();
	}
}
