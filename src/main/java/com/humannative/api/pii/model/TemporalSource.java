package com.humannative.api.pii.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.NotNull;

public record TemporalSource(Long dataSetId, Long dataId, CorpusType corpusType, Reason reason, Violation violation,
							 LocalDateTime start, LocalDateTime end)

		implements
		CorpusSource {
	@JsonCreator
	public TemporalSource(@NotNull Long dataSetId, @NotNull Long dataId, @NotNull TemporalCorpusType corpusType,
			@NotNull Reason reason, @NotNull Violation violation, @NotNull LocalDateTime start,
			@NotNull LocalDateTime end) {
		this(dataSetId, dataId, CorpusType.temporalToCorpusType(corpusType), reason, violation, start, end);
	}
}
