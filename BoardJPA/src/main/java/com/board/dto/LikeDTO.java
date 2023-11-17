package com.board.dto;

import java.time.LocalDateTime;

import com.board.entity.BoardEntity;
import com.board.entity.LikeEntity;
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
public class LikeDTO {
	
	private BoardEntity seqno; 
	private MemberEntity email;
	private String mylikecheck;
	private String mydislikeckeck ;
	private String likedate;
	private String dislikedate;
	
	//Entity -> DTO 이동
	public LikeDTO(LikeEntity entity) {
		this.seqno = entity.getSeqno();
		this.email = entity.getEmail();
		this.mylikecheck = entity.getMylikecheck();
		this.mydislikeckeck = entity.getMydislikeckeck();
		this.likedate = entity.getLikedate();
		this.dislikedate = entity.getDislikedate();
	}
	
	//DTO -> Entity 이동
	public LikeEntity dtoToEntity(LikeDTO dto) {
		
		LikeEntity entity = LikeEntity.builder()
				.seqno(dto.getSeqno())
				.email(dto.getEmail())
				.mylikecheck(dto.getMylikecheck())
				.mydislikeckeck(dto.getMydislikeckeck())
				.likedate(dto.getLikedate())
				.dislikedate(dto.getDislikedate())
				.build();
				
		return entity;
	}
	
}
