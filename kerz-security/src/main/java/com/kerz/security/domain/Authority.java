package com.kerz.security.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;

import com.kerz.jpa.NamedPersistable;

@Entity
public class Authority extends NamedPersistable<Long> implements GrantedAuthority
{
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	@ManyToOne
	private Account account;
	
	protected Authority()
	{
	}
	
	public Authority(String authority)
	{
		super(authority);
	}
	
	@Override
	public String getAuthority()
	{
		return getName();
	}
	
	// package protected
	void setAccount(Account account)
	{
		this.account = account;
	}
	
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
