package com.humannative.common.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class TimeService {

	public LocalDateTime now() {
		return LocalDateTime.now();
	}

}
