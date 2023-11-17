package com.board.service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.board.dto.AddressDTO;
import com.board.dto.MemberDTO;
import com.board.entity.AddressEntity;
import com.board.entity.MemberEntity;
import com.board.entity.repository.AddressRepository;
import com.board.entity.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
	
	//@Autowired
	//MemberMapper mapper;
	
	private final MemberRepository memberRepository;
	private final AddressRepository addressRepository;
	private final BCryptPasswordEncoder pwdEncoder;
	
	//회원가입
	@Override
	public void signUpMember(MemberDTO member) throws Exception {
		//mapper.signUpMember(member1);
		
		member.setRegdate(LocalDateTime.now());
		member.setLastpwdate(LocalDateTime.now());
		member.setPwdchk(1);
		member.setRole("USER");
		member.setFromSocial("N");
		memberRepository.save(member.dtoToEntity(member));
	}
	
	//회원정보 가져오기
	@Override
	public MemberDTO memberInfo(String email) {
		//return mapper.memberInfo(userid);
		return memberRepository.findById(email).map(member -> new MemberDTO(member)).get();
	}
	
	//아이디 중복확인
	@Override
	public int idCheck(String email) throws Exception {
		//return mapper.idCheck(userid);
		return memberRepository.findById(email).isEmpty()? 0:1;
	}
	
	//패스워드 수정
	@Override
	public void pwdModify(MemberDTO member) throws Exception {
		//mapper.pwdModify(member1);
		MemberEntity memberEntity = memberRepository.findById(member.getEmail()).get();
		memberEntity.setPassword(pwdEncoder.encode(member.getPassword()));
		memberRepository.save(memberEntity);
	}
	
	//마지막 로그인 날짜
	@Override
	public void lastlogindateUpdate(String email){
		//mapper.lastlogindateUpdate(userid);
		MemberEntity memberEntity = memberRepository.findById(email).get();
		memberEntity.setLastlogindate(LocalDateTime.now());
		memberRepository.save(memberEntity);
	}
	
	//마지막 로그아웃 날짜
	@Override
	public void lastlogoutdateUpdate(String email) throws Exception {
		//mapper.lastlogoutdateUpdate(userid);
		MemberEntity memberEntity = memberRepository.findById(email).get();
		memberEntity.setLastlogoutdate(LocalDateTime.now());
		memberRepository.save(memberEntity);
	}
	
	//[아이디 찾기]페이지에서 where username = #{username} and telno = #{telno} 
	@Override
	public String searchID(MemberDTO member) throws Exception {
		//return mapper.searchID(member1);
		//return memberRepository.findByUsernameAndTelno(member.getUsername(), member.getTelno()).map(m -> m.get)
		return memberRepository.findByUsernameAndTelno(member.getUsername(), member.getTelno()).map(m->m.getEmail()).orElse("ID_NOT_FOUND");
		//Optional은 stream을 쓸 수 있다. orElse("ID_NOT_FOUND"); 이건 콘솔에러
	}
	
	//임시 패스워드 생성
	@Override
	public String tempPasswordMaker() {
		//숫자 + 영문대소문자 7자리 임시패스워드 생성
		StringBuffer tempPW = new StringBuffer();
		Random rnd = new Random();
		for (int i = 0; i < 7; i++) {
		    int rIndex = rnd.nextInt(3);
		    switch (rIndex) {
		    case 0:
		        // a-z : 아스키코드 97~122
		    	tempPW.append((char) ((int) (rnd.nextInt(26)) + 97));
		        break;
		    case 1:
		        // A-Z : 아스키코드 65~122
		    	tempPW.append((char) ((int) (rnd.nextInt(26)) + 65));
		        break;
		    case 2:
		        // 0-9
		    	tempPW.append((rnd.nextInt(10)));
		        break;
		    }
		}		
		return tempPW.toString();	
	}
	
	//주소 찾기
	@Override
	public Page<AddressEntity> addrSearch(int pageNum, int postNum, String keyword) throws Exception {
		//return mapper.addrSearch(data);
		PageRequest pageRequest = PageRequest.of(pageNum-1,postNum, Sort.by(Direction.ASC,"zipcode"));
		return addressRepository.findByRoadContainingOrBuildingContaining(keyword, keyword, pageRequest);
	}

}
