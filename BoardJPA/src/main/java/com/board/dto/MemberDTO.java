package com.board.dto;

import java.time.LocalDateTime;

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
public class MemberDTO {
	
	private String email;
	private String username;
	private String password;
	private String job;
	private String gender;
	private String hobby;
	private String telno;
	private String nickname;
	private String zipcode;
	private String address; 
	private String description;
	private LocalDateTime regdate; 
	private LocalDateTime lastlogindate;
	private LocalDateTime lastpwdate; 
	private int pwdchk; 
	private String role; 
	private String org_filename; 
	private String stored_filename; 
	private long filesize;
	private LocalDateTime lastlogoutdate;
	private String authkey;
	private String fromSocial;	
	
	//Entity -> DTO 이동
	public MemberDTO(MemberEntity entity) {
        this.email = entity.getEmail();
        this.username = entity.getUsername();
        this.password = entity.getPassword();
        this.job = entity.getJob();
        this.gender = entity.getGender();
        this.hobby = entity.getHobby();
        this.telno = entity.getTelno();
        this.nickname = entity.getNickname();
        this.zipcode = entity.getZipcode();
        this.address = entity.getAddress();
        this.description = entity.getDescription();
        this.regdate = entity.getRegdate();
        this.lastlogindate = entity.getLastlogindate();
        this.lastpwdate = entity.getLastpwdate();
        this.lastlogoutdate = entity.getLastlogoutdate();
        this.pwdchk = entity.getPwdchk();
        this.role = entity.getRole();
        this.org_filename = entity.getOrg_filename();
        this.stored_filename = entity.getStored_filename();
        this.filesize = entity.getFilesize();
        this.authkey = entity.getAuthkey();
        this.fromSocial = entity.getFromSocial();
		
	}
	
	//DTO -> Entity 이동
	public MemberEntity dtoToEntity(MemberDTO dto) {
		
		MemberEntity entity = MemberEntity.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .job(dto.getJob())
                .gender(dto.getGender())
                .hobby(dto.getHobby())
                .telno(dto.getTelno())
                .nickname(dto.getNickname())
                .zipcode(dto.getZipcode())
                .address(dto.getAddress())
                .description(dto.getDescription())
                .regdate(dto.getRegdate())
                .lastlogindate(dto.getLastlogindate())
                .lastpwdate(dto.getLastpwdate())
                .lastlogoutdate(dto.getLastlogoutdate())
                .pwdchk(dto.getPwdchk())
                .role(dto.getRole())
                .org_filename(dto.getOrg_filename())
                .stored_filename(dto.getStored_filename())
                .filesize(dto.getFilesize())
                .authkey(dto.getAuthkey())
                .fromSocial(dto.getFromSocial())
                .build();
		
		return entity;
	}

	@Override
	public String toString() {
		return "MemberDTO [email=" + email + ", username=" + username + ", password=" + password + ", job=" + job
				+ ", gender=" + gender + ", hobby=" + hobby + ", telno=" + telno + ", nickname=" + nickname
				+ ", zipcode=" + zipcode + ", address=" + address + ", description=" + description + ", regdate="
				+ regdate + ", lastlogindate=" + lastlogindate + ", lastpwdate=" + lastpwdate + ", pwdchk=" + pwdchk
				+ ", role=" + role + ", org_filename=" + org_filename + ", stored_filename=" + stored_filename
				+ ", filesize=" + filesize + ", lastlogoutdate=" + lastlogoutdate + ", authkey=" + authkey
				+ ", fromSocial=" + fromSocial + "]";
	}
	
}
