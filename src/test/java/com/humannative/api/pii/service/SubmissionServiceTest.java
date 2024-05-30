package com.humannative.api.pii.service;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import com.humannative.api.pii.dao.PiiDao;
import com.humannative.api.pii.model.CorpusType;
import com.humannative.api.pii.model.Reason;
import com.humannative.api.pii.model.SubmissionDTO;
import com.humannative.api.pii.model.SubmissionReceipt;
import com.humannative.api.pii.model.SubmissionState;
import com.humannative.api.pii.model.TextSource;
import com.humannative.api.pii.model.Violation;
import com.humannative.common.service.CorpusService;
import com.humannative.common.service.TimeService;
import com.humannative.security.model.Role;
import com.humannative.security.model.UserRole;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class SubmissionServiceTest {

	private SubmissionService submissionService;

	private static final UserRole API_USER = new UserRole("apiUser", EnumSet.of(Role.ROLE_USER));

	private static final UserRole ADMIN_USER = new UserRole("apiUser", EnumSet.of(Role.ROLE_USER, Role.ROLE_ADMIN));

	private static final TextSource TEXT_SOURCE = new TextSource(11L, 22L, Reason.FULL_NAME, Violation.CPRA, 11L, 22L);

	private static final SubmissionDTO SUBMISSION_DTO = new SubmissionDTO(1L, API_USER.userName(),
			LocalDate.of(2022, 3, 22), SubmissionState.PENDING, TEXT_SOURCE);

	@Mock
	private PiiDao piiDaoMock;

	@Mock
	private TimeService timeServiceMock;

	@Mock
	private CorpusService corpusService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		submissionService = new SubmissionService(piiDaoMock, timeServiceMock, corpusService);
		when(timeServiceMock.now()).thenReturn(LocalDate.of(2001, 11, 22).atStartOfDay());
		when(corpusService.isDataSetVisible(any(UserRole.class), anyLong(), anyLong())).thenReturn(true);
		when(piiDaoMock.isIdAssoicatedWithUser(anyString(), anyLong())).thenReturn(true);
	}

	@Test
	void when_processSubmission_is_called_verify_SubmissionReceipt_is_returned() {
		SubmissionDTO submissionDTO = new SubmissionDTO(1L, API_USER.userName(), timeServiceMock.now().toLocalDate(),
				SubmissionState.PENDING, TEXT_SOURCE);
		when(piiDaoMock.writeSubmission(any(SubmissionDTO.class))).thenReturn(submissionDTO);
		assertThat(submissionService.processSubmission(API_USER, TEXT_SOURCE),
				is(new SubmissionReceipt(1L, SubmissionState.PENDING)));
	}

	@Test
	void when_processSubmission_returns_404_when_a_corpus_is_unavailable() {
		when(corpusService.isDataSetVisible(any(UserRole.class), anyLong(), anyLong())).thenReturn(false);
		Exception exception = assertThrows(ResponseStatusException.class, () -> {
			submissionService.processSubmission(API_USER, TEXT_SOURCE);
		});
		assertThat(exception.getMessage(), is("404 NOT_FOUND"));
	}

	@Test
	void when_UpdateSubmission_is_called_verify_SubmissionReceipt_is_returned() {

		when(piiDaoMock.updateSubmission(anyLong(), any(SubmissionState.class))).thenReturn(SUBMISSION_DTO);
		assertThat(submissionService.updateSubmission(1L, SubmissionState.PENDING), is(new SubmissionReceipt(1L, SubmissionState.PENDING)));
	}

	@Test
	void when_getSubmission_is_called_returns_SubmissionReceipt() {
		when(piiDaoMock.getSubmission(anyLong())).thenReturn(SUBMISSION_DTO);
		assertThat(submissionService.getSubmission(API_USER, 1L), is(new SubmissionReceipt(1L, SubmissionState.PENDING)));
	}

	@Test
	void when_getSubmission_is_called_with_invalid_Id_throws_404() {
		when(piiDaoMock.getSubmission(anyLong())).thenReturn(SUBMISSION_DTO);
		when(piiDaoMock.isIdAssoicatedWithUser(anyString(), anyLong())).thenReturn(false);
		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> submissionService.getSubmission(API_USER, 1L));

		assertThat(exception.getMessage(), is("404 NOT_FOUND"));
	}

	@Test
	void when_getSubmission_is_called_with_admin_returns_SubmissionReceipt() {
		when(piiDaoMock.getSubmission(anyLong())).thenReturn(SUBMISSION_DTO);
		when(piiDaoMock.isIdAssoicatedWithUser(anyString(), anyLong())).thenReturn(false);

		assertThat(submissionService.getSubmission(ADMIN_USER, 1L), is(new SubmissionReceipt(1L, SubmissionState.PENDING)));
	}

	@Test
	void when_deleteSubmission_is_called_with_API_user_and_valid_id_SubmissionReceipt_is_called() {
		when(piiDaoMock.getSubmission(anyLong())).thenReturn(SUBMISSION_DTO);
		when(piiDaoMock.deleteSubmission(anyLong())).thenReturn(SUBMISSION_DTO);

		assertThat(submissionService.deleteSubmission(API_USER, 1L), is(new SubmissionReceipt(1L, SubmissionState.PENDING)));
	}

	@Test
	void when_deleteSubmission_is_called_with_API_user_and_throws_404_when_id_does_not_exist() {
		when(piiDaoMock.getSubmission(anyLong())).thenReturn(SUBMISSION_DTO);
		when(piiDaoMock.deleteSubmission(anyLong())).thenReturn(SUBMISSION_DTO);
		when(piiDaoMock.isIdAssoicatedWithUser(anyString(), anyLong())).thenReturn(false);

		ResponseStatusException exception =
				assertThrows(ResponseStatusException.class, () -> submissionService.deleteSubmission(API_USER, 1L));
		assertThat(exception.getMessage(), is("404 NOT_FOUND"));
	}

	@Test
	void when_deleteSubmission_is_called_with_API_user_submission_is_not_pending_throw_409() {
		when(piiDaoMock.getSubmission(anyLong())).thenReturn(new SubmissionDTO(SubmissionState.PROCESSING, SUBMISSION_DTO));
		when(piiDaoMock.deleteSubmission(anyLong())).thenReturn(SUBMISSION_DTO);

		ResponseStatusException exception =
				assertThrows(ResponseStatusException.class, () -> submissionService.deleteSubmission(API_USER, 1L));
		assertThat(exception.getMessage(), is("409 CONFLICT"));
	}

	@Test
	void when_getSubmissions_is_called_with_CorpusType_returns_list_of_SubmissionDTO() {
		when(piiDaoMock.getAllSubmissionsByType(any(CorpusType.class))).thenReturn(List.of(SUBMISSION_DTO));
		assertThat(submissionService.getSubmissions(CorpusType.TEXT), is(List.of(SUBMISSION_DTO)));
	}

}
