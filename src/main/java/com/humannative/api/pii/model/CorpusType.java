package com.humannative.api.pii.model;

public enum CorpusType {

	IMAGE, VIDEO, AUDIO, ANIMATION, TEXT;

	public static CorpusType temporalToCorpusType(TemporalCorpusType temporalCorpusType) {
		return CorpusType.valueOf(temporalCorpusType.name());
	}

}
