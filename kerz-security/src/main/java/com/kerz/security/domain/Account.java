package com.kerz.security.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kerz.jpa.VersionedPersistable;

@Entity
public class Account extends VersionedPersistable<Long> implements UserDetails
{
	private static final long serialVersionUID = 1L;
	
	private boolean accountExpired = false;
	private boolean accountLocked = false;
	
	private String activationCode;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "account")
	private Set<Authority> authorities = new HashSet<Authority>();
	
	private boolean credentialsExpired = false;
	@NotNull
	@Size(min = 2, max = 100)
	private String email;
	
	private boolean enabled = false;
	@NotNull
	@Size(min = 2, max = 50)
	private String password;
	private String salt;
	
	protected Account()
	{
	}
	
	public Account(String email, String password)
	{
		super();
		this.email = email;
		this.password = password;
	}
	
	public String getActivationCode()
	{
		return activationCode;
	}
	
	@Override
	public Collection<GrantedAuthority> getAuthorities()
	{
		return Collections.unmodifiableCollection(new ArrayList<GrantedAuthority>(authorities));
	}
	
	public String getEmail()
	{
		return email;
	}
	
	@Override
	public String getPassword()
	{
		return password;
	}
	
	public String getSalt()
	{
		return salt;
	}
	
	@Override
	public String getUsername()
	{
		return email;
	}
	
	public void grantAuthority(String authorityName)
	{
		Authority authority = new Authority(authorityName);
		authority.setAccount(this);
		authorities.add(authority);
	}
	
	public boolean hasAuthority(String authorityName)
	{
		return authorities.contains(new Authority(authorityName));
	}
	
	@Override
	public boolean isAccountNonExpired()
	{
		return !accountExpired;
	}
	
	@Override
	public boolean isAccountNonLocked()
	{
		return !accountLocked;
	}
	
	@Override
	public boolean isCredentialsNonExpired()
	{
		return !credentialsExpired;
	}
	
	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setAccountExpired(boolean accountExpired)
	{
		this.accountExpired = accountExpired;
	}
	
	public void setAccountLocked(boolean accountLocked)
	{
		this.accountLocked = accountLocked;
	}
	
	public void setActivationCode(String activationCode)
	{
		this.activationCode = activationCode;
	}
	
	public void setCredentialsExpired(boolean credentialsExpired)
	{
		this.credentialsExpired = credentialsExpired;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public void setSalt(String salt)
	{
		this.salt = salt;
	}
	
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
