package com.kerz.security.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.kerz.security.domain.Account;

public interface AccountService extends UserDetailsService
{
	Account activate(String username, String activationCode);
	
	Account getByEmail(String username);
	
	Account register(Account user);
}
