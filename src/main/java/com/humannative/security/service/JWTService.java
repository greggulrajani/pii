package com.humannative.security.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Service
public class JWTService {

	public static final String HUMANAI = "humanai";

	private final String key;

	private final long expiry;

	private final Algorithm algorithm;

	public JWTService(@Value("${security.jwt.key:abc123}") String key,
			@Value("${security.jwt.expiry:60000}") long expiry) {
		this.key = key;
		this.expiry = expiry;
		this.algorithm = Algorithm.HMAC512(key);
	}

	public String generateToken(Authentication authentication) {
		return JWT.create()
			.withSubject(authentication.getName())
			.withIssuer(HUMANAI)
			.withExpiresAt(Instant.ofEpochMilli(System.currentTimeMillis() + expiry))
			.sign(algorithm);
	}

	public String validateAndGetUserFromToken(String token) {
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(HUMANAI).build();
		try {
			return verifier.verify(token).getSubject();
		}
		catch (JWTVerificationException jwtVerificationException) {
			throw new BadTokenException(jwtVerificationException.getMessage());
		}
	}

	public long getExpiryTime() {
		return expiry;
	}

	public static class BadTokenException extends RuntimeException {

		public BadTokenException(String message) {
			super(message);
		}

	}

}
