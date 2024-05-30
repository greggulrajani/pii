package com.humannative.security;

import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.humannative.security.model.Role;
import com.humannative.security.utils.SecurityUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SecurityUtilsTest {

	@Test
	void verify_GrantedAuthoritiesToRoles_returns_roles_when_pass_grantedAuth() {
		List<GrantedAuthority> simpleGrantedAuthorities = List.of(new SimpleGrantedAuthority(Role.ROLE_USER.name()),
				new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()));
		assertThat(SecurityUtils.GrantedAuthoritiesToRoles(simpleGrantedAuthorities),
				is(EnumSet.of(Role.ROLE_USER, Role.ROLE_ADMIN)));
	}

}
