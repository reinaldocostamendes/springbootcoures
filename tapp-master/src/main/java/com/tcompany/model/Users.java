package com.tcompany.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
@Getter
@Setter
public class Users implements UserDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private int user_id;
	@Column(name = "email")
	private String email;
	@Column(name = "firsname")
	private String firs_name;
	
	@Column(name = "lastname")
	private String last_name;

	@Column(name = "password")
	private String password;
	
	@Column(name = "enabled")
	private boolean enabled;

	@Override
	public int hashCode() {
		return Objects.hash(enabled, firs_name, last_name, user_id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Users other = (Users) obj;
		return enabled == other.enabled && Objects.equals(firs_name, other.firs_name)
				&& Objects.equals(last_name, other.last_name) && user_id == other.user_id;
	}

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

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
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

