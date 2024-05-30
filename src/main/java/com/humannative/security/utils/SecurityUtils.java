package com.humannative.security.utils;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.humannative.security.model.Role;

public class SecurityUtils {

	public static List<GrantedAuthority> rolesToGrantedAuthority(EnumSet<Role> roles) {
		return roles.stream().map(r -> new SimpleGrantedAuthority(r.name())).collect(Collectors.toList());
	}

	public static EnumSet<Role> GrantedAuthoritiesToRoles(Collection<? extends GrantedAuthority> grantedAuthorities) {

		EnumSet roleSet = EnumSet.noneOf(Role.class);
		for (GrantedAuthority ga : grantedAuthorities) {
			roleSet.add(Role.valueOf(ga.getAuthority()));
		}
		return roleSet;
	}

}
