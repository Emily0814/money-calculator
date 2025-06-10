package com.ajaajas.calc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 클래스.
 * 모든 요청에 대해 인증을 요구하지 않으며, CSRF 보호 기능은 비활성화
 * (개발 편의를 위해 설정한 것으로, 운영 환경에서는 CSRF 활성화 권장)
 * 
 * @author ajaajas
 * @version 1.0
 * @since 2025
 */
@Configuration	//이 클래스가 설정 클래스임을 알려주는 어노테이션
@EnableWebSecurity	//Spring Security를 활성화하는 어노테이션
public class SecurityConfig {

	/**
	 * 보안 필터 체인 설정 메서드
	 * 
	 * @param http 웹 보안 설정을 위한 HttpSecurity 빌더 객체
	 * @return 설정된 SecurityFilterChain 객체
	 * @throws Exception 설정 과정 중 예외 발생 가능
	 */
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
	 * [요약]
	 * - 모든 URL 요청을 인증 없이 허용한다.
	 * - CSRF 보호 기능을 비활성화한다.
	 * - 이 설정으로 인해 /login 페이지로 리다이렉트 되지 않고, 바로 접근 가능하다.
	 */
	
}
