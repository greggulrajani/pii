package com.humannative.api.pii.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.humannative.api.pii.dao.PiiDao;
import com.humannative.api.pii.model.CorpusSource;
import com.humannative.api.pii.model.CorpusType;
import com.humannative.api.pii.model.SubmissionDTO;
import com.humannative.api.pii.model.SubmissionReceipt;
import com.humannative.api.pii.model.SubmissionState;
import com.humannative.common.service.CorpusService;
import com.humannative.common.service.TimeService;
import com.humannative.security.model.Role;
import com.humannative.security.model.UserRole;

@Service
public class SubmissionService {

	private final PiiDao piiDao;

	private final TimeService timeService;

	private final CorpusService corpusService;

	private static final Logger LOG = LoggerFactory.getLogger(SubmissionService.class);

	public SubmissionService(PiiDao piiDao, TimeService timeService, CorpusService corpusService) {
		this.piiDao = piiDao;
		this.timeService = timeService;
		this.corpusService = corpusService;
	}

	public SubmissionReceipt processSubmission(UserRole userRole, CorpusSource submission) {
		if (!corpusService.isDataSetVisible(userRole, submission.dataSetId(), submission.dataSetId())) {
			throw new ResponseStatusException(HttpStatusCode.valueOf(404));
		}

		SubmissionDTO submissionDTO = new SubmissionDTO(userRole.userName(), timeService.now().toLocalDate(),
				SubmissionState.PENDING, submission);

		submissionDTO = piiDao.writeSubmission(submissionDTO);
		return dtoToReceipt(submissionDTO);
	}

	public SubmissionReceipt updateSubmission(long submissionId, SubmissionState submissionState) {
		SubmissionDTO submissionDTO = piiDao.updateSubmission(submissionId, submissionState);
		return dtoToReceipt(submissionDTO);
	}

	public SubmissionReceipt getSubmission(UserRole userRole, long submissionId) {
		isUserAssociatedWithSubmission(userRole, submissionId);
		SubmissionDTO submission = piiDao.getSubmission(submissionId);
		return dtoToReceipt(submission);
	}

	public SubmissionReceipt deleteSubmission(UserRole userRole, long submissionId) {
		isUserAssociatedWithSubmission(userRole, submissionId);
		SubmissionDTO submission = piiDao.getSubmission(submissionId);
		if (!submission.state().equals(SubmissionState.PENDING)) {
			throw new ResponseStatusException(HttpStatusCode.valueOf(409));
		}
		SubmissionDTO submissionDTO = piiDao.deleteSubmission(submissionId);
		return dtoToReceipt(submissionDTO);
	}

	public List<SubmissionDTO> getSubmissions(CorpusType corpusType) {
		return piiDao.getAllSubmissionsByType(corpusType);
	}

	private static SubmissionReceipt dtoToReceipt(SubmissionDTO dto) {
		return new SubmissionReceipt(dto.id(), dto.state());
	}

	private void isUserAssociatedWithSubmission(UserRole userRole, long submissionId) {
		if (userRole.roles().contains(Role.ROLE_ADMIN)) {
			return;
		}

		if (!piiDao.isIdAssoicatedWithUser(userRole.userName(), submissionId)) {
			throw new ResponseStatusException(HttpStatusCode.valueOf(404));
		}
	}

}
