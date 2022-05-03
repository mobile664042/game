package com.simple.game.server.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.simple.game.ddz.domain.manager.DdzGameManager;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class GameEngine {

	@Bean
    public DdzGameManager ddzGameManager() {
		DdzGameManager ddzGameManager = DdzGameManager.buildDefault();
		log.info("游戏配置读取完毕");
		ddzGameManager.init();
		log.info("游戏配置初使化完成");
        return ddzGameManager;
    }
}
