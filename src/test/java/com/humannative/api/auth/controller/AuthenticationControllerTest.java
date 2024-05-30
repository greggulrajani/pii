package com.humannative.api.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.humannative.api.auth.model.AuthenticationResponse;
import com.humannative.api.pii.controller.PiiController;
import com.humannative.security.service.JWTService;
import com.humannative.security.service.SessionService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PiiController controller;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private SessionService sessionServiceMock;

	@MockBean
	private AuthenticationManager authenticationManagerMock;

	@MockBean
	private JWTService jwtServiceMock;

	@MockBean
	private Authentication authenticationMock;

	@Test
	void verify_login_returns_200_and_valid_AuthenticationResponse() throws Exception {
		when(authenticationManagerMock.authenticate(any(Authentication.class))).thenReturn(authenticationMock);
		when(authenticationMock.isAuthenticated()).thenReturn(true);
		when(jwtServiceMock.generateToken(any(Authentication.class))).thenReturn("abc123");
		when(jwtServiceMock.getExpiryTime()).thenReturn(2000L);

		AuthenticationResponse authenticationResponse = new AuthenticationResponse("abc123", 2000L);
		mockMvc.perform(post("/public/login")
						.queryParam("username", "apiUser")
						.queryParam("password", "secret"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(mapper.writeValueAsString(authenticationResponse)));
	}

	@Test
	void verify_login_returns_401_and_no_AuthenticationResponse() throws Exception {
		when(authenticationManagerMock.authenticate(any(Authentication.class))).thenReturn(authenticationMock);
		when(authenticationMock.isAuthenticated()).thenReturn(false);

		mockMvc.perform(post("/public/login")
						.queryParam("username", "apiUser")
						.queryParam("password", "secret"))
				.andDo(print())
				.andExpect(status().isUnauthorized());
	}

	@Test
	void verify_login_returns_401_and_no_AuthenticaionResponse_when_no_params_sent() throws Exception {
		when(authenticationManagerMock.authenticate(any(Authentication.class))).thenReturn(authenticationMock);
		when(authenticationMock.isAuthenticated()).thenReturn(false);

		mockMvc.perform(post("/public/login"))
				.andDo(print())
				.andExpect(status().isUnauthorized());
	}
}
