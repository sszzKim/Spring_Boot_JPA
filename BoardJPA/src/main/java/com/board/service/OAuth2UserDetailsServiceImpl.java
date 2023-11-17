package com.board.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.board.dto.MemberOAuth2DTO;
import com.board.entity.MemberEntity;
import com.board.entity.repository.MemberRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class OAuth2UserDetailsServiceImpl extends DefaultOAuth2UserService{
	
	private final PasswordEncoder pwdEncoder;
	private final MemberRepository memberRepository;
	private final HttpSession session;
	
	@Override
		public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
			//userRequest -> 구글이 응답해준 정보들이 담겨있는 객체
			
			OAuth2User oAuth2User = super.loadUser(userRequest);
			
			String provider = userRequest.getClientRegistration().getRegistrationId();
			String providerId = oAuth2User.getAttribute("sub");
			String email = oAuth2User.getAttribute("email");
			
			log.info("provider: {}"+provider);
			log.info("providerId: {}"+providerId);
			log.info("email: {}"+email);
			
			oAuth2User.getAttributes().forEach((k,v)->{
				log.info(k+":"+v);
			});
			
			MemberEntity member = saveSocialMember(email);
			
			//Role값
			List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
			SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRole());
			grantedAuthorities.add(grantedAuthority);
			
			MemberOAuth2DTO memberOAuth2DTO = new MemberOAuth2DTO();
			memberOAuth2DTO.setAttribute(oAuth2User.getAttributes());
			memberOAuth2DTO.setAuthorities(grantedAuthorities);
			memberOAuth2DTO.setName(member.getUsername());
			
			//WebSecurityConfig.java에서 셋팅하는게 맞는데 안 먹을 때가 있다..?!
			session.setAttribute("email", email);
			session.setAttribute("username", member.getUsername());
			session.setAttribute("role", member.getRole());
			session.setAttribute("FromSocial", "Y");
			
			return memberOAuth2DTO;
		}
	
	private MemberEntity saveSocialMember(String email) {
		//구글 회원 계정으로 로그인 한 회원인 경우 
		//사이트 운영에 필요한 최소한의 정보를 가공해서 tbl_member에 입력
		
		Optional<MemberEntity> result = memberRepository.findById(email);
		if(result.isPresent()) {
			return result.get();
		}
		
		MemberEntity member = MemberEntity.builder()
				.email(email)
				.username("구글회원") //임의로 할당하기 그래서 email 사용
				.password(pwdEncoder.encode("12345"))
				.role("USER")
				.regdate(LocalDateTime.now())
				.fromSocial("Y")
				.build();
		
		memberRepository.save(member);
		
		return member;
	}
}
