package com.humannative.security;

import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.humannative.security.model.Role;
import com.humannative.security.model.UserRole;
import com.humannative.security.service.SessionService;
import com.humannative.security.utils.SecurityUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SessionServiceTest {

	@Test
	void verify_auth_token_is_returned_from_context() {
		List<GrantedAuthority> grantedAuthorities = SecurityUtils.rolesToGrantedAuthority(EnumSet.of(Role.ROLE_USER));
		UsernamePasswordAuthenticationToken userName = new UsernamePasswordAuthenticationToken("userName", null, grantedAuthorities);
		SecurityContextHolder.getContext().setAuthentication(userName);

		assertThat(new SessionService().getAuthTokenFromContext(), is(userName));
	}

	@Test
	void verify_UserRole_is_returned_from_getUserRoleFromContext() {
		List<GrantedAuthority> grantedAuthorities = SecurityUtils.rolesToGrantedAuthority(EnumSet.of(Role.ROLE_USER));
		UsernamePasswordAuthenticationToken userName = new UsernamePasswordAuthenticationToken("userName", null, grantedAuthorities);
		SecurityContextHolder.getContext().setAuthentication(userName);

		assertThat(new SessionService().getUserRoleFromContext(), is(new UserRole("userName", EnumSet.of(Role.ROLE_USER))));

	}

}
