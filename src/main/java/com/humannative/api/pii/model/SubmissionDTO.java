package com.humannative.api.pii.model;

import java.time.LocalDate;

public record SubmissionDTO(Long id, String username, LocalDate submissionDate, SubmissionState state,
		CorpusSource corpusSource) {

	public SubmissionDTO(String username, LocalDate submissionDate, SubmissionState state, CorpusSource corpusSource) {
		this(null, username, submissionDate, state, corpusSource);
	}

	public SubmissionDTO(Long id, SubmissionDTO submissionDTO) {
		this(id, submissionDTO.username, submissionDTO.submissionDate, submissionDTO.state, submissionDTO.corpusSource);
	}

	public SubmissionDTO(SubmissionState state, SubmissionDTO submissionDTO) {
		this(submissionDTO.id, submissionDTO.username, submissionDTO.submissionDate, state, submissionDTO.corpusSource);
	}
}
