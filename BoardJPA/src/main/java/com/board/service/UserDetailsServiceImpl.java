package com.board.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.board.dto.MemberDTO;
import com.board.entity.MemberEntity;
import com.board.entity.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	
	//private final MemberService service;
	private final MemberRepository memberRepository;
	
	//가로(인터셉트)채서 인증처리를 해준다.?!?!
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		//MemberDTO memberInfo =  service.memberInfo(username);//username은 스프링 시큐리티가 필터로 작동하면서 로그인 요청에서 가로채온 userid임 
		MemberEntity memberInfo = memberRepository.findById(username).get();
		
		if(memberInfo == null) {
			throw new UsernameNotFoundException("UsernameNotFoundException: 아이디가 존재하지 않습니다."); //콘솔쪽에 나오는 에러
		}
		
		//SimpleGrantedAuthority 여러개의 사용자 ROLE 값을 받는 객체
		List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
		SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(memberInfo.getRole()); //LIST 타입
		grantedAuthorities.add(grantedAuthority);
		
		User user = new User(username, memberInfo.getPassword(), grantedAuthorities);//USER는 사용자 인증 관련 최상위 객체
		
		return user;
	}
	

}
