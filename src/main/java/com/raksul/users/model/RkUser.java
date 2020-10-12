package com.raksul.users.model;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name="users")
public class RkUser {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	private String email;
	public String getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	public String getUpdationTime() {
		return updationTime;
	}
	public void setUpdationTime(String updationTime) {
		this.updationTime = updationTime;
	}

	private String password;
	private String creationTime;
	private long tokenCreationTime;
	public long getTokenCreationTime() {
		return tokenCreationTime;
	}
	public void setTokenCreationTime(long tokenCreationTime) {
		this.tokenCreationTime = tokenCreationTime;
	}

	private String updationTime;
	private String secret;
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	

	

}
