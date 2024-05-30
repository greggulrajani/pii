package com.humannative.api.pii.model;

public interface CorpusSource {

	Long dataSetId();

	Long dataId();

	CorpusType corpusType();

	Reason reason();

	Violation violation();

}
