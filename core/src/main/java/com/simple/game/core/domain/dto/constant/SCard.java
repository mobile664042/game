package com.simple.game.core.domain.dto.constant;

import com.simple.game.core.exception.BizException;

public enum SCard {
	WEAK_KING(200, 30, 0),
	STRONG_KING(201, 30, 1),
	
	c10(10, 14, 0),
	c11(11, 14, 1),
	c12(12, 14, 2),
	c13(13, 14, 3),
	
	c20(20, 20, 0),
	c21(21, 20, 1),
	c22(22, 20, 2),
	c23(23, 20, 3),
	
	c30(30, 3, 0),
	c31(31, 3, 1),
	c32(32, 3, 2),
	c33(33, 3, 3),
	
	c40(40, 4, 0),
	c41(41, 4, 1),
	c42(42, 4, 2),
	c43(43, 4, 3),
	
	c50(50, 5, 0),
	c51(51, 5, 1),
	c52(52, 5, 2),
	c53(53, 5, 3),
	
	c60(60, 6, 0),
	c61(61, 6, 1),
	c62(62, 6, 2),
	c63(63, 6, 3),
	
	c70(70, 7, 0),
	c71(71, 7, 1),
	c72(72, 7, 2),
	c73(73, 7, 3),
	
	c80(80, 8, 0),
	c81(81, 8, 1),
	c82(82, 8, 2),
	c83(83, 8, 3),
	
	c90(90, 9, 0),
	c91(91, 9, 1),
	c92(92, 9, 2),
	c93(93, 9, 3),
	
	c100(100, 10, 0),
	c101(101, 10, 1),
	c102(102, 10, 2),
	c103(103, 10, 3),
	
	c110(110, 11, 0),
	c111(111, 11, 1),
	c112(112, 11, 2),
	c113(113, 11, 3),
	
	c120(120, 12, 0),
	c121(121, 12, 1),
	c122(122, 12, 2),
	c123(123, 12, 3),
	
	c130(130, 13, 0),
	c131(131, 13, 1),
	c132(132, 13, 2),
	c133(133, 13, 3);
	
	
	
	int face;
	int value;
	int order;
	SCard(int face, int value, int order){
		this.face = face;
		this.value = value;
		this.order = order;
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
	
	public static SCard valueOf(int face) {
		for(SCard scard : SCard.values()) {
			if(scard.getFace() == face) {
				return scard;
			}
		}
		throw new BizException("无效的牌型：" + face);
	}
}
