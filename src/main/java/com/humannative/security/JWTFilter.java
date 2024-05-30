package com.humannative.security;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.humannative.security.model.UserDAO;
import com.humannative.security.model.UserRole;
import com.humannative.security.service.JWTService;
import com.humannative.security.utils.SecurityUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter extends OncePerRequestFilter {

	private final UserDAO userDAO;

	private final JWTService jwtService;

	private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

	public JWTFilter(UserDAO userDAO, JWTService jwtService) {
		this.userDAO = userDAO;
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		jwtTokenFromHeader(request.getHeader("Authorization")).ifPresent(token -> {
			try {
				String userName = jwtService.validateAndGetUserFromToken(token);
				UserRole user = userDAO.getUser(userName);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						user.userName(), null, SecurityUtils.rolesToGrantedAuthority(user.roles()));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
			catch (JWTService.BadTokenException exception) {
				logger.debug("Bad token exception ", exception);
			}
		});
		filterChain.doFilter(request, response);
	}

	private static Optional<String> jwtTokenFromHeader(String authHeader) {
		if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
			return Optional.of(authHeader.substring(7));
		}
		return Optional.empty();
	}

}
