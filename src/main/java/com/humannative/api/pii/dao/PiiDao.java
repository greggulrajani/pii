package com.humannative.api.pii.dao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.humannative.api.pii.model.CorpusType;
import com.humannative.api.pii.model.SubmissionDTO;
import com.humannative.api.pii.model.SubmissionState;

@Repository
public class PiiDao {

	private final List<SubmissionDTO> submissionDTOS;

	private final AtomicLong counter = new AtomicLong();

	public PiiDao() {
		submissionDTOS = new ArrayList<>();
	}

	public PiiDao(List<SubmissionDTO> submissionDTOS) {
		this.submissionDTOS = submissionDTOS;
	}

	public SubmissionDTO writeSubmission(SubmissionDTO submission) {
		SubmissionDTO submissionDTOWithId = new SubmissionDTO(counter.addAndGet(1), submission);
		submissionDTOS.add(submissionDTOWithId);
		return submissionDTOWithId;
	}

	public SubmissionDTO getSubmission(long submissionId) {
		return submissionDTOS.stream().filter(f -> f.id().equals(submissionId)).findFirst().orElseThrow();
	}

	public SubmissionDTO deleteSubmission(long submissionId) {
		SubmissionDTO submission = getSubmission(submissionId);
		if (!submissionDTOS.remove(submission)) {
			throw new IllegalStateException("Could not delete item");
		}
		return submission;
	}

	public List<SubmissionDTO> getAllSubmissionsByType(CorpusType corpusType) {
		return submissionDTOS.stream()
				.filter(f -> f.corpusSource().corpusType().equals(corpusType))
				.sorted(Comparator.comparing(SubmissionDTO::submissionDate).thenComparing(SubmissionDTO::username))
				.collect(Collectors.toList());
	}

	public SubmissionDTO updateSubmission(long submissionId, SubmissionState submissionState) {
		SubmissionDTO submissionDTO = getSubmission(submissionId);

		SubmissionDTO updatedSubDTO = new SubmissionDTO(submissionState, submissionDTO);
		if (!submissionDTOS.remove(submissionDTO)) {
			throw new IllegalStateException("Could not delete item");
		}
		submissionDTOS.add(updatedSubDTO);
		return updatedSubDTO;
	}

	public boolean isIdAssoicatedWithUser(String username, long submissionId) {
		SubmissionDTO submission = submissionDTOS.stream()
				.filter(f -> f.id().equals(submissionId))
				.findFirst()
				.orElse(null);

		if (submission == null) {
			return false;
		}
		return submission.username().equals(username);
	}

}
