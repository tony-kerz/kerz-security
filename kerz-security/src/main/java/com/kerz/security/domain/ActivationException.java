package com.kerz.security.domain;

import com.kerz.text.FormattedException;

public class ActivationException extends FormattedException
{
	private static final long serialVersionUID = 1L;
	
	public ActivationException(String pattern, Object... arguments)
	{
		super(pattern, arguments);
	}
}
