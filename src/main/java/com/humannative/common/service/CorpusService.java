package com.humannative.common.service;

import org.springframework.stereotype.Service;

import com.humannative.security.model.Role;
import com.humannative.security.model.UserRole;

@Service
public class CorpusService {

	public boolean isDataSetVisible(UserRole userRole, long dataSetId, long dataId) {
		if (userRole.roles().contains(Role.ROLE_ADMIN)) {
			return true;
		}

		if (dataSetId == 42) {
			return false;
		}
		return true;
	}

}
