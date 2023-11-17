package com.board;

import org.springframework.beans.BeanMetadataAttribute;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.board.service.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
@Log4j2
public class WebSecurityConfig {
	
	private final AuthSuccessHandler authSuccessHandler;
	private final AuthFailureHandler authFailureHandler;
	
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final OAuth2FailureHandler oAuth2FailureHandler;
	
	private final UserDetailsServiceImpl uDetailsService;

	// 스프링시큐리티에서 암호화 관련 객체를 가져다가 스프링빈으로 등록
	@Bean
	public BCryptPasswordEncoder pwdEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	//스프링 시큐리티 적용 제외 대상
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/images/**","/css/**","/profile/**","/js/**");
	}
	
	//spring security 로그인 화면 사용비활성화, csrf, cors 공격 방어용 보안 설정 비활
	//같은 세션기반 보안 레벨이기때문에 스프링 시큐리티를 쓴다고 해서 보안 레벨이 높아지지는 않는다.
	@Bean
	SecurityFilterChain filer(HttpSecurity http) throws Exception {
		//http.formLogin().disable().csrf().disable().cors.disable();

		//http.formLogin((login)-> login.disable());
		//스프링시큐리티 FormLogin 설정
		http.formLogin((login)-> login.usernameParameter("email"). //spring security에서 사용하는 id변수명 username 그래서, 사용자 지정 id 변수명을 이렇게
				loginPage("/member/login"). //사용자 지정 login 화면 및 명령문을 사용할 때 그 경로를 스프링 시큐리티에게 공지
				successHandler(authSuccessHandler). //로그인성공 시 class
				failureHandler(authFailureHandler)); //실패 시 class
		
		//스프링시큐리티 자동 로그인 설정
		http.rememberMe((me)-> me.key("sszz1").
				alwaysRemember(false).
				tokenValiditySeconds(3600*24*7).
				rememberMeParameter("remember-me").
				userDetailsService(uDetailsService).
				authenticationSuccessHandler(authSuccessHandler));
		
		//OAuth2
		http.oauth2Login((login)-> login
				.loginPage("/member/login")
				.successHandler(oAuth2SuccessHandler)
				.failureHandler(oAuth2FailureHandler)
		);
		
		//세션 설정
		http.sessionManagement(management -> 
		management.maximumSessions(1)
		.maxSessionsPreventsLogin(false) //동시 접속자 몰렸을 때 자르는 것
		.expiredUrl("/member/login")
		);
		
		//로그아웃
		http
		.logout( logout -> logout
				.logoutUrl("/member/logout")
				.logoutSuccessUrl("/member/login")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID","remember-me")//JSESSIONID 자동생성된 쿠키, remember-me 내가 만든 쿠키
				.permitAll()
				);
		
		
		//스프링시큐리티의 접근 권한 설정(Authenticaion)
		http.authorizeHttpRequests((authz) -> authz.requestMatchers("/member/**").permitAll()
				.requestMatchers("/board/**").hasAnyAuthority("USER","MASTER")
				.requestMatchers("/master/**").hasAnyAuthority("MASTER")
				.anyRequest().authenticated());
		
		//csrf,cors 비활성화
		http.csrf((csrf)->csrf.disable());
		http.cors((cors)->cors.disable());
		
		log.info("스프링 시큐리티 설정 완료!"); //logging
		
		return http.build();

	}
	 

}
