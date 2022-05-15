package com.simple.game.ddz.domain.dto.constant.ddz;

/****
 * 
 * 加倍方式
 * 
 * @author zhibozhang
 *
 */
public enum DoubleKind {
	
	/****累加翻倍(炸一次在底注的基础上加多一倍，新手玩法)****/
	cumulation,

	/****指数翻倍(默认，炸一次在之前的基础上加多一倍，刺激玩法)****/
	exponential;
	
	/***
	 * 计算结果
	 * 
	 * @param unitPrice
	 * @param doubleCount
	 * @return
	 */
	public long calcResult(long unitPrice, int doubleCount) {
		if(this == cumulation) {
			return unitPrice * (doubleCount);
		}
		
		long result = unitPrice;
		for(int i=0; i<doubleCount-1; i++) {
			result *= 2;
		}
		return result;
	}
}
