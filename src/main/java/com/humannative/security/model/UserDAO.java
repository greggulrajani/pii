package com.humannative.security.model;

import java.util.EnumSet;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class UserDAO {

	private static UserRole API_USER = new UserRole("apiUser", EnumSet.of(Role.ROLE_USER));

	private static UserRole ADMIN_USER = new UserRole("adminUser", EnumSet.of(Role.ROLE_USER, Role.ROLE_ADMIN));

	private static UserRole NO_USER = new UserRole("", EnumSet.of(Role.ROLE_NONE));

	private List<UserRole> USER_ROLES = List.of(API_USER, ADMIN_USER);

	public UserRole getUser(String userName) {
		return USER_ROLES.stream().filter(f -> f.userName().equals(userName)).findFirst().orElse(NO_USER);
	}

}
