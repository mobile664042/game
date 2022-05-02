package com.simple.game.core.domain.cmd;

/*****
****请求code 从100000开始,高3位，低3位
****
*   push.code从1100000开始,高4位，低3位, 其中第1位表示是推送, 其他与req.code对应
*   如果没有对应的req.code, 则第1位是2，
*
*****/