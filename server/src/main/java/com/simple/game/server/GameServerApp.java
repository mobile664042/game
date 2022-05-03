package com.simple.game.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import lombok.extern.slf4j
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class GameServerApp /* implements ApplicationRunner */ {
	public static void main(String[] args) {
		log.info("游戏准备启动-->");
		SpringApplication.run(GameServerApp.class, args);
		log.info("游戏启动完成！！");
	}

}
