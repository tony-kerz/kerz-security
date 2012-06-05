package com.kerz.security.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.util.UriTemplate;

import com.kerz.mail.MailSender;
import com.kerz.security.dao.jpa.AccountDao;
import com.kerz.security.domain.Account;
import com.kerz.security.domain.ActivationException;
import com.kerz.util.MessageSourceHelper;
import com.kerz.util.StringHelper;

@Component
public class AccountServiceImpl implements AccountService, InitializingBean, MessageSourceAware
{
	private Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
	private AccountDao accountDao;
	private PasswordEncoder passwordEncoder;
	private SaltSource saltSource;
	private MailSender mailSender;
	private MessageSource messageSource;
	private UriTemplate activationUriTemplate;
	private String activationMailFromEmail;
	
	public void setMailSender(MailSender mailSender)
	{
		this.mailSender = mailSender;
	}
	
	public void setMessageSource(MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}
	
	public void setActivationMailFromEmail(String activationMailFromEmail)
	{
		this.activationMailFromEmail = activationMailFromEmail;
	}
	
	public void setActivationUrlPattern(String pattern)
	{
		activationUriTemplate = new UriTemplate(pattern);
	}
	
	private String getActivationUrl(String userId, String activationCode)
	{
		return activationUriTemplate.expand(userId).toString() + "?activationCode=" + activationCode;
	}
	
	@Override
	public Account activate(String username, String activationCode)
	{
		Account user = getByEmail(username);
		if (user == null)
		{
			throw new ActivationException("unable to locate user={0}", username);
		}
		String actual = passwordEncoder.encodePassword(activationCode, saltSource.getSalt(user));
		String expected = user.getActivationCode();
		if (actual.equals(expected))
		{
			user.setEnabled(true);
			user.grantAuthority("ROLE_USER");
			accountDao.save(user);
		}
		else
		{
			throw new ActivationException(
			    "activation code does not match for user={0}, expected={1}, actual={2}, actual-raw={3}", username, expected,
			    actual, activationCode);
		}
		return user;
	}
	
	@Override
	public Account getByEmail(String username)
	{
		return accountDao.findByEmail(username);
		// Account result = null;
		// List<Account> users = accountDao.findByEmail(username);
		// if (users != null)
		// {
		// if (users.size() == 1)
		// {
		// result = users.get(0);
		// }
		// else if (users.size() > 1)
		// {
		// throw new
		// FormattedException("unexpected multiple users found for username={0}",
		// username);
		// }
		// }
		// return result;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException
	{
		Account result = getByEmail(username);
		if (result == null)
		{
			log.debug("unable to find username={}", username);
			throw new UsernameNotFoundException(username);
		}
		return result;
	}
	
	public void setPasswordEncoder(PasswordEncoder passwordEncoder)
	{
		this.passwordEncoder = passwordEncoder;
	}
	
	public void setSaltSource(SaltSource saltSource)
	{
		this.saltSource = saltSource;
	}
	
	@Autowired
	public void setAccountDao(AccountDao accountDao)
	{
		this.accountDao = accountDao;
	}
	
	public Account register(Account user)
	{
		String activationCode = StringHelper.getRandomString(10);
		user.setSalt(StringHelper.getRandomString(10));
		Object salt = saltSource.getSalt(user);
		user.setActivationCode(passwordEncoder.encodePassword(activationCode, salt));
		user.setPassword(passwordEncoder.encodePassword(user.getPassword(), salt));
		user = accountDao.save(user);
		postRegistrationHook(user, activationCode);
		return user;
	}
	
	protected void postRegistrationHook(Account user, String activationCode)
	{
		if (mailSender != null)
		{
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("user", user);
			model.put("activationUrl", getActivationUrl(user.getEmail(), activationCode));
			try
			{
				mailSender.send(activationMailFromEmail, user.getEmail(),
				    MessageSourceHelper.getMessage(messageSource, "activation-mail-subject"), "activation-email", model);
			}
			catch (Throwable t)
			{
				log.warn("incurred exception attempting to send mail, ignoring...", t);
			}
		}
		else
		{
			log.warn("mailSender == null, activating user automatically...");
			activate(user.getEmail(), activationCode);
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception
	{
		Assert.notNull(passwordEncoder, "password encoder required");
		Assert.notNull(saltSource, "salt source required");
		Assert.notNull(accountDao, "user dao required");
		// Assert.notNull(mailSender, "mail sender required");
	}
}
