package com.board.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.board.entity.BoardEntity;
import com.board.entity.FileEntity;
import com.board.entity.MemberEntity;
import com.board.entity.ReplyEntity;

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
public class ReplyDTO {
	
	private Long replyseqno;
	//private BoardEntity seqno;
	private Long seqno;
	private String replywriter;
	private String replycontent;
	private LocalDateTime replyregdate;
	//private MemberEntity email;
	private String email;
	
	//Entity -> DTO 이동
	public ReplyDTO(ReplyEntity entity) {
		this.replyseqno = entity.getReplyseqno();
		this.seqno = entity.getSeqno().getSeqno();
		this.replywriter = entity.getReplywriter();
		this.replycontent = entity.getReplycontent();
		this.replyregdate = entity.getReplyregdate();
		this.email = entity.getEmail().getEmail();
	}
	
	//DTO -> Entity 이동
	
	
}
