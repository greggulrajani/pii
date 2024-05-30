package com.humannative.api.pii.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.NotNull;

public record ImageSource(@NotNull Long dataSetId, @NotNull Long dataId, @NotNull CorpusType corpusType,
		@NotNull Reason reason, @NotNull Violation violation, @NotNull Long x, @NotNull Long y, @NotNull Long width,
		@NotNull Long height) implements CorpusSource {

	@JsonCreator
	public ImageSource(@NotNull Long dataSetId, @NotNull Long dataId, @NotNull Reason reason,
			@NotNull Violation violation, @NotNull Long x, @NotNull Long y, @NotNull Long width, @NotNull Long height) {
		this(dataSetId, dataId, CorpusType.IMAGE, reason, violation, x, y, width, height);
	}
}
