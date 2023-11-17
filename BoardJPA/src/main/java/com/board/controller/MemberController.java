package com.board.controller;

import java.io.File;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.board.dto.MemberDTO;
import com.board.entity.AddressEntity;
import com.board.service.MemberService;
import com.board.util.PageUtil;

@Controller
public class MemberController {
	
	@Autowired
	MemberService service;
	
	@Autowired
	private BCryptPasswordEncoder pwdEncoder;
	
	//로그인 화면 보기
	@GetMapping("/member/login")
	public void getLogin(){}
	
	//로그인??
	@PostMapping("/member/login")
	public void postLogin(){}
	
	//로그인 
	@ResponseBody
	@PostMapping("/member/loginCheck") //시큐리티 설정으로 변경
	public String postLogin(MemberDTO member, HttpSession session) throws Exception{
		
		//id가 있는지 check
		if(service.idCheck(member.getEmail()) == 0){ return "{\"message\":\"ID_NOT_FOUND\"}";}
		
		//pwd check
		if(!pwdEncoder.matches(member.getPassword(), service.memberInfo(member.getEmail()).getPassword())){ //일치 시 TRUE
			 return "{\"message\":\"PASSWORD_NOT_FOUND\"}";
		}
		
		return "{\"message\":\"GOOD\"}"; 
		
	}
	
	//회원등록 화면 보기	
	@GetMapping("/member/signup")
	public void getSignup() {}
	
	//회원등록
	@ResponseBody
	@PostMapping("/member/signup")
	public String postSignup(MemberDTO member, @RequestParam("fileUpload") MultipartFile multipartFile) throws Exception{
		
		String path = "C:/Repository/profile/";
		File targetFile;
		
		String org_filename="";
		String org_fileExtension="";
		String stored_filename="";
		
		if(!multipartFile.isEmpty()){ //empty가 아니면
			System.out.print("파일: ");
			org_filename = multipartFile.getOriginalFilename();
			org_fileExtension = org_filename.substring(org_filename.lastIndexOf(".")); //확장자 추출
			stored_filename = UUID.randomUUID().toString().replaceAll("-", "") + org_fileExtension;	 //-들어왔을 경우 제거	
			
		}
		
		try {
			System.out.print("파일1: "+org_filename);
			System.out.print("파일2: "+org_fileExtension);
			System.out.print("파일3: "+stored_filename);
			targetFile = new File(path + stored_filename);
			multipartFile.transferTo(targetFile);
			member.setOrg_filename(org_filename);
			member.setStored_filename(stored_filename);
			member.setFilesize(multipartFile.getSize());
			
			System.out.print("파일4ise: "+member.getFilesize());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//패스워드 암호화
		String inputPassword = member.getPassword();
		String encPwd = pwdEncoder.encode(inputPassword); //단방향 암호화, 복구불가 
		member.setPassword(encPwd);
		member.setLastpwdate(LocalDateTime.now());
		
		member.setRole("USER");
		member.setPwdchk(1);
		
		System.out.println("회원가입:" +member.toString());
		service.signUpMember(member);
		
		//Map<String, String> data = new HashMap<String, String>();
		//data.put("message", "GOOD");
		//data.put("username", URLEncoder.encode(member.getUsername(),"UTF-8"));
		//return data;
		// Map -> JSON 으로 변환해주는 라이브러리는 jackson.databind
		
		return "{\"message\":\"GOOD\",\"username\":\""+ URLEncoder.encode(member.getUsername(),"UTF-8") +"\"}"; 
				
	}
	
	//회원 가입 시 아이디 중복 확인
	@ResponseBody
	@PostMapping("/member/idCheck")
	public int  postIdCheck(@RequestBody String userid) throws Exception {
		int result1 = service.idCheck(userid);
		//System.out.print("■□▢▣▤▥▦▧▨▩▪▫▬▭▮▯▰▱▲△▴: "+result1);
		return result1;
	}
	
	//주소검색
	@GetMapping("/member/addrSearch")
	public void addrSearch( @RequestParam("page") int pageNum,
			@RequestParam(name="keyword", defaultValue="", required=false) String keyword, Model model) throws Exception{
		
		int postNum = 10; //한 화면에 보여지는 게시물 행의 갯수
		//int startPoint = (pageNum-1)*postNum+1; //오라클은 1부터
		//int endPoint = pageNum*postNum;
		int pageListCount = 10; //화면 하단에 보여지는 페이지리스트의 페이지 갯수
		//int totalCount = service.addrTotalCount(keyword);
		Page<AddressEntity> list = service.addrSearch(pageNum, postNum, keyword);
		int totalCount = (int)list.getTotalElements();
		
		//model.addAttribute("list",service.addrSearch(startPoint, endPoint, keyword));
		
		PageUtil page = new PageUtil();
		model.addAttribute("list", list);
		model.addAttribute("pageList", page.getPageAddress(pageNum, postNum, pageListCount, totalCount, keyword));
		
	}
	
	//로그아웃
	@GetMapping("/member/logout")
	public String getLogout(HttpSession session) throws Exception {
		//마지막 로그아웃 날짜 등록
		service.lastlogoutdateUpdate((String)session.getAttribute("userid"));
		//session.invalidate();//모든 세션 종료
		
		return "redirect:/";
	}
	
	//회원정보 화면 보기
	@GetMapping("/board/memberinfo")
	public void getMemberinfo(Model model, HttpSession session) throws Exception{
		MemberDTO member = service.memberInfo((String)session.getAttribute("userid"));
		model.addAttribute("view", member);
	}
	
	@GetMapping("/board/memberinfoModify")
	public void getMemberinfoModify() throws Exception{
	}
	
	//패스워드변경 화면 보기
	@GetMapping("/member/memberPasswordModify")
	public void getMemberPasswordModify(Model model, HttpSession session) throws Exception{
	}
	
	//패스워드변경
	@ResponseBody
	@PostMapping("/member/memberPasswordModify")
	public Map<String, String> postMemberPasswordModify(Model model, HttpSession session,
			@RequestBody Map<String, String> pwdDataMap) throws Exception{
		
		Map<String, String> json = new HashMap<String, String>();
		
		//기존 패스워드 가져오기
		//MemberDTO selected_member = service.memberInfo((String)session.getAttribute("userid"));
		String email = (String)session.getAttribute("email");
		
		if(!pwdEncoder.matches(pwdDataMap.get("oldpwd"), service.memberInfo(email).getPassword())){ //일치 시 TRUE
			//return "{\"message\":\"PASSWORD_NOT_FOUND\"}";
			json.put("message", "MISMATCH");
		}else{
			//패스워드 암호화
			/*String inputPassword = pwdDataMap.get("newpwd1");
			String encPwd = pwdEncoder.encode(inputPassword); //단방향 암호화, 복구불가 
			MemberDTO member = new MemberDTO();
			member.setUserid((String)session.getAttribute("userid"));
			member.setPassword(encPwd);
			member.setLastpwdate(LocalDate.now());
			service.pwdModify(member); //aukey 삭제도 같이 함
			
			//마지막 로그아웃 시간 업데이트
			//모든 세션 종료
			service.lastlogoutdateUpdate((String)session.getAttribute("userid"));
			session.invalidate();
			*/
			MemberDTO member = new MemberDTO();
			member.setEmail(email);
			member.setPassword(pwdEncoder.encode(pwdDataMap.get("newpwd1")));
			member.setLastpwdate(LocalDateTime.now());
			service.pwdModify(member);
			
			json.put("message", "GOOD");
		}
		
		return json;
	}
	
	//아이디 찾기
	@GetMapping("/member/searchID")
	public void getSearchID() throws Exception {}
	
	//아이디 찾기
	@ResponseBody
	@PostMapping("/member/searchID")
	public Map<String, String>  postSearchID(@RequestBody Map<String, String> map, Model model) throws Exception {
		
		Map<String, String> json = new HashMap<String, String>();
		
		MemberDTO member = new MemberDTO();
		member.setUsername(map.get("username"));
		member.setTelno(map.get("telno"));
		
		if(service.searchID(member)!=null) {
			json.put("message", "GOOD");
			json.put("userid",service.searchID(member));
		}else {
			json.put("message", "MISMATCH");
		}
		
		return json;
	}
	
	//비밀번호 찾기 보기
	@GetMapping("/member/searchPassword")
	public void getSearchPassword() throws Exception {}
	
	//비밀번호 찾기
	@ResponseBody
	@PostMapping("/member/searchPassword")
	public Map<String, String>  postSearchPassword(@RequestBody Map<String, String> map, Model model) throws Exception {
		
		Map<String, String> json = new HashMap<String, String>();
		
		//email check
		if(service.idCheck(map.get("email"))==0){
			json.put("message","MISMATCH" );
		}
		
		if(!service.memberInfo(map.get("email")).getTelno().equals(map.get("telno"))) {
			json.put("message","MISMATCH" );
		}
		
		String newPWD =service.tempPasswordMaker();
		MemberDTO member = new MemberDTO();
		
		member.setEmail(map.get("email"));
		member.setPassword(pwdEncoder.encode(newPWD)) ;
		member.setLastpwdate(LocalDateTime.now());
		service.pwdModify(member);
		
		json.put("message", "GOOD");
		json.put("tempPWD", newPWD);
		
		return json;
	}
	
	//로그인 시 패스워드 변경 여부 확인
	@GetMapping("/member/pwCheckNotice")
	public void getPwCheckNotice() throws Exception{
		
	}
	
	@PostMapping("/member/invalidate")
	@ResponseBody
	public void postInvalidate(HttpSession session) {
		session.invalidate();
	}
	
}
