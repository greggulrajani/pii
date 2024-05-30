package com.humannative.security;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.humannative.security.model.Role;
import com.humannative.security.model.UserDAO;
import com.humannative.security.model.UserRole;
import com.humannative.security.utils.SecurityUtils;

@Service
public class LocalAuthenticationProvider implements AuthenticationProvider {

	private final UserDAO userDAO;

	public LocalAuthenticationProvider(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UserRole user = userDAO.getUser(authentication.getPrincipal().toString());
		List<GrantedAuthority> grantedAuthorities = SecurityUtils.rolesToGrantedAuthority(user.roles());

		if (user.roles().contains(Role.ROLE_NONE)) {
			return UsernamePasswordAuthenticationToken.unauthenticated(user.userName(), grantedAuthorities);
		}

		return new UsernamePasswordAuthenticationToken(user.userName(), authentication.getCredentials(),
				grantedAuthorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}
}
