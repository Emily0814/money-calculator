package com.ajaajas.calc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 스프링부트 애플리케이션의 시작점
 * 내장 서버를 실행하고, 스프링 컨텍스트를 초기화
 * 
 * @author ajaajas
 * @version 1.0
 * @since 2025
 */
@SpringBootApplication
public class CalcApplication {

	public static void main(String[] args) {
		//스프링부트 애플리케이션 실행
		SpringApplication.run(CalcApplication.class, args);
	}
	
}
