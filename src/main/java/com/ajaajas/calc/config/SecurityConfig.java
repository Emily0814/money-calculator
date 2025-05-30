package com.ajaajas.calc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration	//이 클래스가 설정 클래스임을 알려주는 어노테이션
@EnableWebSecurity	//Spring Security를 활성화하는 어노테이션
public class SecurityConfig {

	@Bean	//스프링 컨테이너에 객체를 등록하는 어노테이션
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {	
		//HttpSecurity: 웹 보안 설정을 위한 매개변수
		//SecurityFilterChain: 보안 필터 체인을 정의하는 인터페이스
		http	//HTTP 요청 권한 설정
			.authorizeHttpRequests(auth -> auth
					.anyRequest().permitAll()	//anyRequest(): 모든 요청에 대해	//permitAll(): 인증 없이 모든 사용자에게 허용
			)	//CSRF(Cross-Site Request Forgery) 공격 방지 설정
			.csrf(csrf -> csrf.disable());	//csrf(): CSRF(Cross-Site Request Forgery) 공격 방지 설정	//csrf.disable(): CSRF 보호 기능을 비활성화
			//개발 단계에서는 편의를 위해 비활성화하지만, 운영 환경에서는 활성화 권장
		return http.build();	//설정된 HttpSecurity 객체를 빌드해서 SecurityFilterChain 반환
	}
	/*
	 
		[요약]
	 	모든 URL 요청을 인증 없이 허용하고, CSRF 보호는 비활성화한다.
	 	이 설정으로 인해 /login으로 리다이렉트되지 않고 바로 원하는 페이지에 접근할 수 있게 된다.
	  
	*/
}
