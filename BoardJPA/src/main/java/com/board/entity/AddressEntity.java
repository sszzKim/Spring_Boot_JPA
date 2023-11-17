package com.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Entity(name="address")
@Table(name="tbl_addr")
public class AddressEntity {
	
	@Id
	@Column(name="seqno")
	private Long seqno; //JPA는 무조건 PK가 있어야해서 생성함, 그리고 int보다 long을 선호
	
	@Column(name="zipcode", length=10, nullable=true)
	private String zipcode;
	
	@Column(name="province", length=50, nullable=true)
	private String province;
	
	@Column(name="road", length=200, nullable=true)
	private String road;
	
	@Column(name="building", length=200, nullable=true)
	private String building;
	
	@Column(name="oldaddr", length=200, nullable=true)
	private String oldaddr;

}
