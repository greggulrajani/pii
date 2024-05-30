package com.humannative.security.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.humannative.security.model.UserRole;
import com.humannative.security.utils.SecurityUtils;

@Service
public class SessionService {

	public Authentication getAuthTokenFromContext() {
		return Optional.ofNullable(SecurityContextHolder.getContext())
			.map(a -> a.getAuthentication())
			.orElse(new UsernamePasswordAuthenticationToken(null, null));
	}

	public UserRole getUserRoleFromContext() {
		return Optional.ofNullable(getAuthTokenFromContext())
			.map(f -> new UserRole(Objects.toString(f.getPrincipal()),
					SecurityUtils.GrantedAuthoritiesToRoles(f.getAuthorities())))
			.orElseThrow();
	}

	public void setAuthTokenToContext(Authentication authentication) {
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

}
