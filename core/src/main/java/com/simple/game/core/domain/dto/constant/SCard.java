package com.simple.game.core.domain.dto.constant;

import com.simple.game.core.exception.BizException;

public enum SCard {
	WEAK_KING(200, 30, 0, 30),
	STRONG_KING(201, 30, 1, 30),
	
	c10(10, 14, 0, 1),
	c11(11, 14, 1, 1),
	c12(12, 14, 2, 1),
	c13(13, 14, 3, 1),
	
	c20(20, 20, 0, 2),
	c21(21, 20, 1, 2),
	c22(22, 20, 2, 2),
	c23(23, 20, 3, 2),
	
	c30(30, 3, 0, 3),
	c31(31, 3, 1, 3),
	c32(32, 3, 2, 3),
	c33(33, 3, 3, 3),
	
	c40(40, 4, 0, 4),
	c41(41, 4, 1, 4),
	c42(42, 4, 2, 4),
	c43(43, 4, 3, 4),
	
	c50(50, 5, 0, 5),
	c51(51, 5, 1, 5),
	c52(52, 5, 2, 5),
	c53(53, 5, 3, 5),
	
	c60(60, 6, 0, 6),
	c61(61, 6, 1, 6),
	c62(62, 6, 2, 6),
	c63(63, 6, 3, 6),
	
	c70(70, 7, 0, 7),
	c71(71, 7, 1, 7),
	c72(72, 7, 2, 7),
	c73(73, 7, 3, 7),
	
	c80(80, 8, 0, 8),
	c81(81, 8, 1, 8),
	c82(82, 8, 2, 8),
	c83(83, 8, 3, 8),
	
	c90(90, 9, 0, 9),
	c91(91, 9, 1, 9),
	c92(92, 9, 2, 9),
	c93(93, 9, 3, 9),
	
	c100(100, 10, 0, 10),
	c101(101, 10, 1, 10),
	c102(102, 10, 2, 10),
	c103(103, 10, 3, 10),
	
	c110(110, 11, 0, 11),
	c111(111, 11, 1, 11),
	c112(112, 11, 2, 11),
	c113(113, 11, 3, 11),
	
	c120(120, 12, 0, 12),
	c121(121, 12, 1, 12),
	c122(122, 12, 2, 12),
	c123(123, 12, 3, 12),
	
	c130(130, 13, 0, 13),
	c131(131, 13, 1, 13),
	c132(132, 13, 2, 13),
	c133(133, 13, 3, 13);
	
	
	
	int face;
	int value;
	int order;
	/***sencondValue，用来扩展顺子的牌型***/
	int sv;
	SCard(int face, int value, int order, int sv){
		this.face = face;
		this.value = value;
		this.order = order;
		this.sv = sv;
	}
	public int getFace() {
		return face;
	}
	public int getValue() {
		return value;
	}
	public int getOrder() {
		return order;
	}
	public int getSv() {
		return sv;
	}
	
	public static SCard valueOf(int face) {
		for(SCard scard : SCard.values()) {
			if(scard.getFace() == face) {
				return scard;
			}
		}
		throw new BizException("无效的牌型：" + face);
	}
}
