package com.humannative.api.pii.model;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SubmissionDTOTest {

	private static final CorpusSource CORPUS_SOURCE = new TextSource(1L, 2L, Reason.BIOMETRIC_DATA, Violation.CCPA, 1L,
			100L);

	private static final SubmissionDTO SUBMISSION_DTO = new SubmissionDTO(11L, "username", LocalDate.MIN,
			SubmissionState.PENDING, CORPUS_SOURCE);

	@Test
	void verify_non_id_ctor_creates_valid_object() {
		SubmissionDTO submissionDTO = new SubmissionDTO("username", LocalDate.MAX, SubmissionState.PENDING,
				CORPUS_SOURCE);
		assertThat(submissionDTO,
				is(new SubmissionDTO(null, "username", LocalDate.MAX, SubmissionState.PENDING, CORPUS_SOURCE)));
	}

	@Test
	void verify_id_copy_ctor_creates_valid_object() {
		assertThat(new SubmissionDTO(22L, SUBMISSION_DTO),
				is(new SubmissionDTO(22L, "username", LocalDate.MIN, SubmissionState.PENDING, CORPUS_SOURCE)));
	}

	@Test
	void verify_state_copy_ctor_creates_valid_object() {
		assertThat(new SubmissionDTO(SubmissionState.REJECTED, SUBMISSION_DTO),
				is(new SubmissionDTO(11L, "username", LocalDate.MIN, SubmissionState.REJECTED, CORPUS_SOURCE)));
	}

}
