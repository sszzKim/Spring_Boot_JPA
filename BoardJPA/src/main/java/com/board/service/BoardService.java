package com.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.board.dto.BoardDTO;
import com.board.dto.FileDTO;
import com.board.dto.LikeDTO;
import com.board.dto.ReplyDTO;
import com.board.dto.ReplyInterface;
import com.board.entity.BoardEntity;
import com.board.entity.FileEntity;
import com.board.entity.LikeEntity;

public interface BoardService {
	//게시물 리스트
	//public List<BoardDTO> list(int startPoint, int endPoint, String keyword) throws Exception;
	public Page<BoardEntity> list(int pageNum, int postNum, String keyword);
	
	//게시물 보기
	public BoardDTO view(Long seqno) throws Exception;
	
	//이전 seqno 가져오기
	public Long pre_seqno(Long seqno, String keyword) throws Exception;
	
	//다음 seqno 가져오기
	public Long next_seqno(Long seqno, String keyword) throws Exception;
	
	//hit 올리기
	public void hitNoUpdate(Long seqno) throws Exception;
	
	//게시물 등록 하기
	public void write(BoardDTO board) throws Exception; 
	
	//게시물 tbl_board_seq NEXTVAL
	public Long getSeqnoNextval() throws Exception; 
		
	//파일업로드
	public void fileUpload(FileDTO fileDTO) throws Exception; 	
	
	//파일 리스트
	public List<FileEntity> getFileList(Long seqno) throws Exception;	
	
	//seqno에 따른 파일체크 update
	public void checkfileUpdate(Long seqno) throws Exception;
	
	//삭제하기
	public void delete(Long seqno) throws Exception;
	
	//수정하기
	public void modify(BoardDTO board) throws Exception;
	
	//파일 select
	public FileDTO getFile(Long fileseqno) throws Exception; 
	
	//게시물 좋아요 싫어요 업데이트
	public void boardLikeUpdate(BoardDTO board) throws Exception;
	
	//게시물 수정 시 파일 정보 수정 checkfile을 X로 변경
	public void deleteFileList(Long fileseqno) throws Exception;
	
	//좋아요 등록여부 확인
	public LikeEntity likeCheckView(Long seqno, String email) throws Exception;
	
	//좋아요 싫어요 신규 등록
	public void likeCheckRegistry(Long seqno, String email, String mylikeCheck, String mydislikeCheck, String likeDate, String dislikeDate) throws Exception;

	//좋아요 싫어요 수정
	public void likeCheckUpdate(Long seqno, String email, String mylikeCheck, String mydislikeCheck, String likeDate, String dislikeDate) throws Exception;	
	
	//댓글 리스트 가져오기
	public List<ReplyInterface> replyView(ReplyInterface reply) throws Exception;
	//댓글 등록
	public void replyRegistry(ReplyInterface reply) throws Exception;
		
	//댓글 수정
	public void replyUpdate(ReplyInterface reply) throws Exception;
		
	//댓글 삭제
	public void replyDelete(ReplyInterface reply) throws Exception;
	
}
