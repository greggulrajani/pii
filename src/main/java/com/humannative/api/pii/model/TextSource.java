package com.humannative.api.pii.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.NotNull;

public record TextSource(Long dataSetId, Long dataId, CorpusType corpusType, Reason reason, Violation violation,
						 Long start, Long end) implements CorpusSource {
	@JsonCreator
	public TextSource(@NotNull Long dataSetId, @NotNull Long dataId, @NotNull Reason reason,
			@NotNull Violation violation, @NotNull Long start, @NotNull Long end) {
		this(dataSetId, dataId, CorpusType.TEXT, reason, violation, start, end);
	}
}
