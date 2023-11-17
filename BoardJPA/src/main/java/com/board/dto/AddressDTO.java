package com.board.dto;

import java.time.LocalDateTime;

import com.board.entity.AddressEntity;

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
public class AddressDTO {
	
	private String zipcode;
	private String province;
	private String road;
	private String building;
	private String oldaddr;
	
	//Entity -> DTO 이동
	public AddressDTO(AddressEntity entity) {
		this.zipcode = entity.getZipcode();
		this.province = entity.getProvince();
		this.road = entity.getRoad();
		this.building = entity.getBuilding();
		this.oldaddr = entity.getOldaddr();
	}
	
	//DTO -> Entity 이동
	public AddressEntity dtoToEntity(AddressDTO dto) {
		
		AddressEntity entity = AddressEntity.builder()
				.zipcode(dto.getZipcode())
				.province(dto.getProvince())
				.road(dto.getRoad())
				.building(dto.getBuilding())
				.oldaddr(dto.getOldaddr())
				.build();
				
		return entity;
	}

}
