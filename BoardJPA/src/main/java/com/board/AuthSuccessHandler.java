package com.board;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.board.dto.MemberDTO;
import com.board.entity.MemberEntity;
import com.board.entity.repository.MemberRepository;
import com.board.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
	
	//private final MemberService service;
	private final MemberRepository memberRepository;
	
	//로그인 성공 시 처리될 명령문들
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		// authentication.getName() = 로그인 시 입력된 userid 값을 가져옴
		//member = service.memberInfo(authentication.getName());
		MemberEntity memberInfo = memberRepository.findById(authentication.getName()).get();
		
		//마지막 로그인 날짜 등록
		//service.lastlogindateUpdate(member.getEmail());
		
		//패스워드 확인 후 변경일 30일 경과 시 처리하는 로직
		
		//세션 생성
		HttpSession session = request.getSession();
		session.setMaxInactiveInterval(3600*24*7); //세션유지기간 = 한시간*24시간*7일
		//session.setAttribute("email", service.memberInfo(member.getEmail()).getEmail());
		//session.setAttribute("username", service.memberInfo(member.getEmail()).getUsername());
		//session.setAttribute("role", service.memberInfo(member.getEmail()).getRole());
		
		session.setAttribute("email", memberInfo.getEmail());
		session.setAttribute("username", memberInfo.getUsername());
		session.setAttribute("role", memberInfo.getRole());
		
		setDefaultTargetUrl("/board/list?page=1");
		super.onAuthenticationSuccess(request, response, authentication);	
		
	}
}
