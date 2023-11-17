package com.board.dto;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;
import lombok.Setter;

@Setter
public class MemberOAuth2DTO implements OAuth2User{ //OAuth2User: OAuth의 사용자를 담을 I/F임 즉 구현체를 하나 만들어야함
	//OAuth2UserDetailsServiceImpl에서 setter로 입력 받은 값을 OAuth2가 읽어들임
	
	private Map<String, Object> attribute;
	private Collection<? extends GrantedAuthority> authorities; //특정시킨다..? Collection(LIST)
	private String name;

	@Override
	public Map<String, Object> getAttributes() {
		return this.attribute;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
}
