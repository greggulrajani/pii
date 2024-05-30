package com.humannative.api.auth.service;

import org.junit.jupiter.api.BeforeEach;

import com.humannative.security.service.JWTService;

public class JWTServiceTest {

	private JWTService service;

	@BeforeEach
	void setUp() {
		service = new JWTService("123", 60000);
	}

}
