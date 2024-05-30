package com.humannative.api.pii.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class CorpusTypeTest {

	@Test
	void verify_temporalToCorpusType_maps_TemporalCorpusType_to_CorpusType() {
		assertThat(CorpusType.temporalToCorpusType(TemporalCorpusType.ANIMATION), is(CorpusType.ANIMATION));
		assertThat(CorpusType.temporalToCorpusType(TemporalCorpusType.VIDEO), is(CorpusType.VIDEO));
		assertThat(CorpusType.temporalToCorpusType(TemporalCorpusType.AUDIO), is(CorpusType.AUDIO));
	}

	@Test
	void verify_all_temporalToCorpusTypes_exist_in_CorpusType() {
		for (TemporalCorpusType type : TemporalCorpusType.values()) {
			assertThat(CorpusType.temporalToCorpusType(type), notNullValue());
		}
	}

}
