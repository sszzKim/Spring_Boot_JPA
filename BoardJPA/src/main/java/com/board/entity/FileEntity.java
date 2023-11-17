package com.board.entity;

import java.util.Date;

import com.board.dto.FileDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
@Entity(name="file")
@Table(name="tbl_file")
public class FileEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILE_SEQ" ) //키 생성 전략 --> 가급적 시퀀스는 DB에 수정적으로 생성하는 방법 추천,DBMS 회사마자 전략이 다름 
	@SequenceGenerator(name="FILE_SEQ", sequenceName = "TBL_FILE_SEQ", initialValue = 1, allocationSize = 1)
	private Long fileseqno; //JPA는 PK는 in보단 long으로 권고
	
	@Column(name="seqno", nullable=false)
	private Long seqno;
	
	@Column(name="org_filename", length=200, nullable=false)
	private String org_filename;
	
	@Column(name="stored_filename", length=200, nullable=false)
	private String stored_filename;
	
	@Column(name="filesize", nullable=false)
	private long filesize;
	
	@Column(name="email", length=200, nullable=false)
	private String email;
	
	@Column(name="checkfile", length=2, nullable=false)
	private String checkfile;
	
}
