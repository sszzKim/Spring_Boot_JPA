package com.board.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.board.dto.AddressDTO;
import com.board.dto.BoardDTO;
import com.board.dto.MemberDTO;
import com.board.entity.AddressEntity;

public interface MemberService {
	
	//멤버 등록 하기
	public void signUpMember(MemberDTO member) throws Exception; 
	
	//id Check
	public int idCheck(String email) throws Exception; 
	
	//마지막 로그인 날짜 update
	public void lastlogindateUpdate(String email); 
	
	//마지막 로그아웃 날짜 update
	public void lastlogoutdateUpdate(String email) throws Exception; 
	
	//memberInfo
	public MemberDTO memberInfo(String email); 
		
	//주소검색
	public Page<AddressEntity> addrSearch(int pageNum, int postNum, String keyword) throws Exception;
	
	//비밀번호 수정
	public void pwdModify(MemberDTO member) throws Exception;
	
	//아이디 찾기
	public String searchID(MemberDTO member) throws Exception;
	
	//임시 패스워드 생성
	public String tempPasswordMaker();
}
