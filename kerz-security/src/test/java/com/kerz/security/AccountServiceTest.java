package com.kerz.security;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kerz.security.domain.Account;
import com.kerz.security.domain.Authority;
import com.kerz.security.service.AccountService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AccountServiceTest
{
	@Autowired
	private AccountService accountService;
	
	@Test
	public void basic()
	{
		Account user = new Account("anthony.kerz@gmail.com", "password1");
		String authority = "king";
		user.grantAuthority(authority);
		user = accountService.register(user);
		user = (Account) accountService.loadUserByUsername(user.getUsername());
		Assert.assertTrue(user.hasAuthority(authority));
	}
	
	@Test
	public void authorityEquality()
	{
//		Set<Authority> s = new HashSet<Authority>();
//		Authority a1 = new Authority("king");
//		System.out.println("a1-hash=" + a1.hashCode());
//		Authority a2 = new Authority("king");
//		System.out.println("a2-hash=" + a2.hashCode());
//		Assert.assertEquals(a1.hashCode(), a2.hashCode());
//		Assert.assertEquals(a1, a2);
//		s.add(a1);
//		s.add(a2);
//		Assert.assertTrue(s.size() == 1);
//		Assert.assertTrue(s.contains(a1));
//		Assert.assertTrue(s.contains(a2));
		setTest(new HashSet<Authority>());
		//setTest(new PersistentSet());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
  private void setTest(Set s)
	{
		Authority a1 = new Authority("king");
		System.out.println("a1-hash=" + a1.hashCode());
		Authority a2 = new Authority("king");
		System.out.println("a2-hash=" + a2.hashCode());
		Assert.assertEquals(a1.hashCode(), a2.hashCode());
		Assert.assertEquals(a1, a2);
		s.add(a1);
		s.add(a2);
		Assert.assertTrue(s.size() == 1);
		Assert.assertTrue(s.contains(a1));
		Assert.assertTrue(s.contains(a2));
	}
}
