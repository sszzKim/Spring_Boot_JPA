package com.board.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Entity(name="board")
@Table(name="tbl_board")
public class BoardEntity {
	
	@Id
	@Column(name="seqno")
	private Long seqno;
	
	@Column(name="writer", length=50, nullable=false)
	private String writer;
	
	@Column(name="title", length=200, nullable=false)
	private String title;
	
	@Column(name="content", length=2000, nullable=false)
	private String content;
	
	@Column(name="regdate", nullable=false)
	private LocalDateTime regdate;
	
	@Column(name="hitno",nullable=true)
	private int hitno;
	
	@Column(name="likecnt",nullable=true)
	private int likecnt;
	
	@Column(name="dislikecnt",nullable=true)
	private int dislikecnt;
	
	//FK
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name="email", nullable=false)
	private MemberEntity email; 
	
}
