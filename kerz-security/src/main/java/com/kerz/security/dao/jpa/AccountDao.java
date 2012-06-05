package com.kerz.security.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kerz.security.domain.Account;

public interface AccountDao extends JpaRepository<Account, Long>
{
	Account findByEmail(String email);
}
