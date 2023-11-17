package com.board.dto;

import java.time.LocalDateTime;
import java.util.Date;

import com.board.entity.BoardEntity;
import com.board.entity.FileEntity;
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
public class FileDTO {
	
	private Long fileseqno;
	private Long seqno;
	private String org_filename;
	private String stored_filename;
	private Long filesize;
	private String email;
	private String checkfile;
	
	//Entity -> DTO 이동
	public FileDTO(FileEntity entity) {
		this.fileseqno = entity.getFileseqno();
		this.seqno = entity.getSeqno();
		this.org_filename = entity.getOrg_filename();
		this.stored_filename = entity.getStored_filename();
		this.filesize = entity.getFilesize();
		this.email = entity.getEmail();
		this.checkfile = entity.getCheckfile();
	}
	
	//DTO -> Entity 이동
	public FileEntity dtoToEntity(FileDTO dto) {
		
		FileEntity entity = FileEntity.builder()
				.fileseqno(dto.getFileseqno())
				.seqno(dto.getSeqno())
				.org_filename(dto.getOrg_filename())
				.stored_filename(dto.getStored_filename())
				.filesize(dto.getFilesize())
				.email(dto.getEmail())
				.checkfile(dto.getCheckfile())
				.build();
				
		return entity;
	}
	
}
