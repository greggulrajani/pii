package com.humannative.api.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.humannative.security.service.JWTService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JWTServiceTest {

	private JWTService service;

	@BeforeEach
	void setUp() {
		service = new JWTService("123", 60000);
	}

	@Test
	void verify_expirtyTime_matches() {
		assertThat(service.getExpiryTime(), is(60000L));
	}
}
