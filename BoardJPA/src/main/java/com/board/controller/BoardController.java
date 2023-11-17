package com.board.controller;

import java.io.File;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.board.dto.ReplyDTO;
import com.board.dto.ReplyInterface;
import com.board.entity.BoardEntity;
import com.board.entity.LikeEntity;
import com.board.dto.BoardDTO;
import com.board.dto.FileDTO;
import com.board.dto.LikeDTO;
import com.board.service.BoardService;
import com.board.util.PageUtil;

@Controller
public class BoardController {
	
	// @Autowired를 이용한 DI(dependency Injection 의존성 주입)
	@Autowired
	BoardService service;
	
	
	/* 2.
	 * 생성자를 이용한 의존성 주입
	 * BoardService service;
	public BoardController(BoardService service) {
		this.service = service;
	}*/
	
	//3. 룸복을 이용해서 의존성 주입 @RequiredArgsConstructor
	//private final BoardService service;
	
	
	//게시물 목록 보기
	@GetMapping("/board/list") //jsp 명이랑 같아야 자동으로 jsp파일로 model이 전달 됨
	public void getList(Model model, @RequestParam("page") int pageNum, 
	@RequestParam(name="keyword", defaultValue="", required=false) String keyword) throws Exception {
		
		int postNum = 5; //한 화면에 보여지는 게시물 행의 갯수
		//int startPoint = (pageNum-1)*postNum+1;
		//int endPoint = pageNum*postNum;
		int pageListCount = 10; //화면 하단에 보여지는 페이지리스트의 페이지 갯수
		//int totalCount = service.totalcount(startPoint,postNum,keyword);
		
		PageUtil page = new PageUtil();
		Page<BoardEntity> list = service.list(pageNum, postNum, keyword);
		int totalCount = (int)list.getTotalElements();
	
		//PageUtil page = new PageUtil();
		model.addAttribute("list", service.list(pageNum,postNum,keyword));
		model.addAttribute("totalElm", totalCount); //추가
		model.addAttribute("postNum", postNum); //추가
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageList", page.getPageList(pageNum, postNum, pageListCount, totalCount, keyword));
	}
	
	//게시물 내용 보기
	@GetMapping("/board/view")
	public void getView(@RequestParam("seqno") Long seqno, Model model, @RequestParam("page") int pageNum, 
			@RequestParam(name="keyword", defaultValue="", required=false) String keyword, HttpSession session) throws Exception {
				
		String session_email = (String)session.getAttribute("email");
		
		System.out.println("session_email: "+session_email);
		
		BoardDTO board = service.view(seqno);
		
		model.addAttribute("view", board);
		model.addAttribute("viewEmail", service.view(seqno).getEmail().getEmail());
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pre_seqno",service.pre_seqno(seqno,keyword));
		model.addAttribute("next_seqno",service.next_seqno(seqno,keyword));
		model.addAttribute("fileList",service.getFileList(seqno));
		
		System.out.print("fileList: "+service.getFileList(seqno).size());
		
		//if(!board.getUserid().equals(session.getAttribute("userid")))
		if(!session_email.equals(board.getEmail().getEmail()))
			service.hitNoUpdate(seqno); //본인껀 안 오르게
		
		//LikeDTO likeDTO = new LikeDTO();
		//String session_userid = (String)session.getAttribute("userid");
		//likeDTO.setSeqno(seqno);
		//likeDTO.setUserid(session_userid);
		//LikeDTO likeDTOView =  service.likeCheckView(likeDTO);
		LikeEntity likeCheckView = service.likeCheckView(seqno, session_email);
		
		if(likeCheckView == null) {
			model.addAttribute("myLikeCheck", "N");
			model.addAttribute("myDislikeCheck", "N");
		}else {
			model.addAttribute("myLikeCheck", likeCheckView.getMylikecheck());
			model.addAttribute("myDislikeCheck", likeCheckView.getMydislikeckeck());
		}
		
		
		
	}
	
	//게시물 등록 화면 보기
	@GetMapping("/board/write")
	public void getWrite() {
	}
	
	//게시물 등록 하기
	@ResponseBody
	@PostMapping("/board/write")
	//public Map<String, String> postWrite(BoardDTO board) throws Exception {
	public String postWrite(BoardDTO board) throws Exception {
		
		Long seqno = service.getSeqnoNextval();
		board.setSeqno(seqno);
		
		service.write(board);
		
		Map<String, String> json = new HashMap<String, String>();
		json.put("message", "GOOD");
		
		return "{\"message\":\"GOOD\"}";
	}
	
	//파일등록
	@ResponseBody
	@PostMapping("/board/fileUpload")
	public Map<String, String> postFileUpload(BoardDTO board, @RequestParam("kind") String kind, 
			@RequestParam("sendToFileList") List<MultipartFile> sendToFileList,
			@RequestParam(name="deleteFileList", required=false) Long[] deleteFileList) throws Exception {
		
		String path = "C:/Repository/file/";
		
		Long seqno = 0L;
		
		if(kind.equals("I")) { //신규
			seqno = service.getSeqnoNextval();
			board.setSeqno(seqno);
			service.write(board); //글 등록
		}else if(kind.equals("U")) { //수정
			service.modify(board);
			seqno = board.getSeqno();
			
			if(deleteFileList != null) {
				for(Long fileseqno :deleteFileList) 
					service.deleteFileList(fileseqno);
			}
		}
		
		if(!sendToFileList.isEmpty()){ //파일 등록
			
			File targetFile = null;
			//Map<String, Object> fileinfo = null; 
			FileDTO fileInfo = null;
			
			for(MultipartFile file : sendToFileList) {
				String org_filename = file.getOriginalFilename();
				String org_fileExtension = org_filename.substring(org_filename.lastIndexOf(".")); //확장자 추출
				String stored_filename = UUID.randomUUID().toString().replaceAll("-", "") + org_fileExtension;	
				long filesize = file.getSize();
				
				try {
					targetFile = new File(path + stored_filename);
					file.transferTo(targetFile);
					
					fileInfo = FileDTO.builder()
							.seqno(seqno)
							.org_filename(org_filename)
							.stored_filename(stored_filename)
							.filesize(filesize)
							.email(board.getEmail().getEmail())
							.checkfile("O")
							.build();
							
					
					/*
					 * fileinfo = new HashMap<String, Object>(); fileinfo.put("seqno", seqno);
					 * fileinfo.put("org_filename", org_filename); fileinfo.put("stored_filename",
					 * stored_filename); fileinfo.put("filesize", filesize); fileinfo.put("userid",
					 * board.getUserid()); fileinfo.put("checkfile", "O"); // O: 파일 존재, X:파일 삭제
					 * fileinfo.put("kind", kind);
					 */
					
					service.fileUpload(fileInfo);
					
				} catch (Exception e) {}
			}
		}
			
		Map<String, String> json = new HashMap<String, String>();
		json.put("message", "GOOD");
		
		return json;
	}
	
	//파일 다운로드
	@GetMapping("/board/fileDownload")
	public void getFileDownload(@RequestParam("fileseqno") Long fileseqno, HttpServletResponse rs) throws Exception{
		
		String path = "C:/Repository/file/";
		
		FileDTO fileinfo = service.getFile(fileseqno);
		//http 해더에 들어가는 내용, 프로토콜 == 정해진 규약 == 넌 따라야해
		String org_filename = fileinfo.getOrg_filename();
		String stored_filename = fileinfo.getStored_filename();
		byte fileByte[] = FileUtils.readFileToByteArray(new File(path+stored_filename));
		
		// Content-Disposition = 헤더에 정의되어 있는 속성
		// 해당 헤더 값과 바이트 배열로 변환된 파일이 담겨 있는 바디를 response하게 되면
		// 이 파일을 다운받게끔 하는 것임
		// filename ="hello.jpg"
		
		rs.setContentType("application/octet-stream");
		rs.setContentLength(fileByte.length); //바디부분 길이
		rs.setHeader("Content-Disposition","attachment; filename=\""+
		URLEncoder.encode(org_filename,"UTF-8")+"\";");
		rs.getOutputStream().write(fileByte);
		rs.getOutputStream().flush();
		rs.getOutputStream().close();
	
	}

	//수정화면 보기
	@GetMapping("/board/modify")
	public void getModify(@RequestParam("seqno") Long seqno, Model model,@RequestParam("page") int pageNum, 
			@RequestParam(name="keyword", defaultValue="", required=false) String keyword) throws Exception {
		model.addAttribute("page", pageNum);
		model.addAttribute("keyword", keyword);
		model.addAttribute("view", service.view(seqno));
		//파일리스트가져오기
		model.addAttribute("fileList",service.getFileList(seqno));
	}
	
	//게시물 수정 하기
	@Transactional
	@ResponseBody
	@PostMapping("/board/modify")
	public String postModify(BoardDTO board, Model model,@RequestParam("page") int pageNum, 
			@RequestParam(name="keyword", defaultValue="", required=false) String keyword,
			@RequestParam(name="deleteFileList", required=false) Long[] deleteFileList) throws Exception {
		service.modify(board);
		String keyword1 = URLEncoder.encode(keyword, "UTF-8");
		//return "redirect:/board/view?seqno="+board.getSeqno()+"&page="+pageNum+"&keyword="+keyword1;
		//return "{\"message\":\"GOOD\"}";
		
		if(deleteFileList != null) {
			for(Long fileseqno :deleteFileList) 
				service.deleteFileList(fileseqno);
		}
		
		return "{\"message\":\"GOOD\"}";
	}
	
	//게시물 삭제 하기
	@Transactional
	@GetMapping("/board/delete")
	public String getDelete(@RequestParam("seqno") Long seqno) throws Exception {
		
		//transaction start
		service.checkfileUpdate(seqno);
		service.delete(seqno);
		//transaction end
		
		return "redirect:/board/list?page=1";
	}
	
	//집에 가고 싶다.
	@ResponseBody
	@PostMapping("/board/likeCheck")
	//public String postLikeCheck(@RequestBody Map<String, Object> likeCheckData) throws Exception{
	public Map<String, String> postLikeCheck(@RequestBody Map<String, Object> likeCheckData) throws Exception{
		
		//Long seqno = (Long)likeCheckData.get("seqno"); //key값 넣기
		int seq = (int)likeCheckData.get("seqno");
        Long seqno = (long)seq;
		String email =  (String)likeCheckData.get("email"); 
		String mylikecheck = (String)likeCheckData.get("mylikecheck"); 
		String mydislikecheck = (String)likeCheckData.get("mydislikecheck"); 
		int checkCnt =  (int)likeCheckData.get("ckeckCnt");
		
		
		//현재 날짜, 시간 구해서 좋아요/싫어요  한 날짜/시간 입력 및 수정
		String likeDate = "";
		String dislikeDate = "";
		LocalDateTime now = LocalDateTime.now();
		if(likeCheckData.get("mylikecheck").equals("Y")) {
			likeDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString();
		}else if(likeCheckData.get("mydislikecheck").equals("Y")) {
			dislikeDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString();
		}
		
		likeCheckData.put("likedate",likeDate);
		likeCheckData.put("dislikedate",dislikeDate);
		
		LikeDTO likeData = new LikeDTO();
		/*
		 * likeData.setSeqno(seqno); likeData.setUserid(userid);
		 * likeData.setMylikecheck(mylikecheck);
		 * likeData.setMydislikeckeck(mydislikecheck); likeData.setLikedate(likeDate);
		 * likeData.setDislikedate(dislikeDate);
		 */
		
		//tbl_like 테이블 입력/수정
		//LikeDTO likeCheckView = service.likeCheckView(likeData);
		LikeEntity likeCheckView = service.likeCheckView(seqno, email);
		//if(likeCheckView == null )service.likeCheckRegistry(likeData);
		if(likeCheckView == null ) service.likeCheckRegistry(seqno, email, mylikecheck, mydislikecheck, likeDate, dislikeDate);
		//else service.likeCheckUpdate(likeData);
		else service.likeCheckUpdate(seqno, email, mylikecheck, mydislikecheck, likeDate, dislikeDate);
		
		int likeCnt = service.view(seqno).getLikecnt();
		int dislikeCnt = service.view(seqno).getDislikecnt();
	
		switch(checkCnt) {
			case 1: {likeCnt--; break;}
			case 2: {likeCnt++; dislikeCnt--; break;}
			case 3: {likeCnt++; break;}
			case 4: {dislikeCnt--; break;}
			case 5: {likeCnt--; dislikeCnt++; break;}
			case 6: {dislikeCnt++; break;}
		}
		
		BoardDTO board = new BoardDTO();
		board.setSeqno(seqno);
		board.setLikecnt(likeCnt);
		board.setDislikecnt(dislikeCnt);
		
		service.boardLikeUpdate(board);
		
		Map<String, String> json = new HashMap<String, String>();
		json.put("message", "GOOD");
		json.put("likeCnt", Integer.toString(likeCnt) );
		json.put("dislikeCnt", Integer.toString(dislikeCnt));
		
		return json;
		//return "{\"likeCnt\":\"" + likeCnt + "\",\"dislikeCnt\": \"" + dislikeCnt + "\"}";
		
	}
	
	//댓글처리
	@ResponseBody
	@PostMapping("/board/reply")
	//public List<ReplyDTO> postReply(@RequestBody ReplyDTO reply,@RequestParam("option") String option) throws Exception{
	public List<ReplyInterface> postReply(ReplyInterface reply,@RequestParam("option") String option) throws Exception{	
		switch(option) {
			case "I" : 
				service.replyRegistry(reply);
				break;
			case "U" : 
				service.replyUpdate(reply);
				break;
			case "D" : 
				service.replyDelete(reply);
				break;
		}
		
		return service.replyView(reply);
	}
	
}
