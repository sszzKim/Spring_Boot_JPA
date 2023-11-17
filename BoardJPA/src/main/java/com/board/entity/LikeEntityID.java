package com.board.entity;

import java.io.Serializable;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode //시리얼라이즈: 변수값들을 Serialize화 하기 위해서 필요한 작업을 해 줌 
public class LikeEntityID implements Serializable{
	private Long seqno;
	private String email;
}
