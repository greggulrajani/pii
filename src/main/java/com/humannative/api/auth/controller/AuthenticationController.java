package com.humannative.api.auth.controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.humannative.api.auth.model.AuthenticationResponse;
import com.humannative.security.service.JWTService;
import com.humannative.security.service.SessionService;

@RestController
@RequestMapping("/public")
public class AuthenticationController {

	private final SessionService sessionService;

	private final AuthenticationManager authenticationManager;

	private final JWTService jwtService;

	public AuthenticationController(SessionService sessionService, AuthenticationManager authenticationManager,
			JWTService jwtService) {
		this.sessionService = sessionService;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@PostMapping("/login")
	ResponseEntity<AuthenticationResponse> login(String username, String password) {
		Authentication authentication = authenticationManager
			.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		if (!authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatusCode.valueOf(401)).build();
		}
		sessionService.setAuthTokenToContext(authentication);
		String token = jwtService.generateToken(authentication);
		long expiry = jwtService.getExpiryTime();
		return ResponseEntity.ok(new AuthenticationResponse(token, expiry));
	}

}
