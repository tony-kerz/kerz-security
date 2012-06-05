package com.kerz.security;

import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityHelper
{
	UserDetails getUserDetails(SecurityContextHolderStrategy secCtx)
	{
		return (UserDetails) secCtx.getContext();
	}
}
