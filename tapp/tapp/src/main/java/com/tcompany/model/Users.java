package com.tcompany.model;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class Users {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private int user_id;
	
	@Column(name = "firsname")
	private String firs_name;
	
	@Column(name = "lastname")
	private String last_name;
	
	@Column(name = "enabled")
	private boolean enabled;

	public Users() {
		super();
		
	}

	public Users(int user_id, String firs_name, String last_name, boolean enabled) {
		super();
		this.user_id = user_id;
		this.firs_name = firs_name;
		this.last_name = last_name;
		this.enabled = enabled;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getFirs_name() {
		return firs_name;
	}

	public void setFirs_name(String firs_name) {
		this.firs_name = firs_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "Users [user_id=" + user_id + ", firs_name=" + firs_name + ", last_name=" + last_name + ", enabled="
				+ enabled + "]";
	}
	
	
}
