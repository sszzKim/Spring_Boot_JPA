package com.board.dto;

import java.time.LocalDateTime;
import java.util.Date;

import com.board.entity.BoardEntity;
import com.board.entity.MemberEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {
	
	private Long seqno;
	private String writer;
	private String title;
	private String content;
	private MemberEntity email;
	private LocalDateTime regdate;
	private int hitno;
	private int likecnt;
	private int dislikecnt;
	
	//Entity -> DTO 이동
	public BoardDTO(BoardEntity boardEntity) {
		this.seqno = boardEntity.getSeqno();
		this.writer = boardEntity.getWriter();
		this.title = boardEntity.getTitle();
		this.content = boardEntity.getContent();
		this.email = boardEntity.getEmail();
		this.regdate = boardEntity.getRegdate();
		this.hitno = boardEntity.getHitno();
		this.likecnt = boardEntity.getLikecnt();
		this.dislikecnt = boardEntity.getDislikecnt();	
	}
	
	//DTO -> Entity 이동
	public BoardEntity dtoToEntity(BoardDTO boardDTO) {
		
		BoardEntity boardEntity = BoardEntity.builder()
				.seqno(boardDTO.getSeqno())
				.writer(boardDTO.getWriter())
				.title(boardDTO.getTitle())
				.content(boardDTO.getContent())
				.email(boardDTO.getEmail())
				.regdate(boardDTO.getRegdate())
				.likecnt(boardDTO.getLikecnt())
				.dislikecnt(boardDTO.getDislikecnt())
				.build();
				
		return boardEntity;
	}
	
	
}
