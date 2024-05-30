package com.humannative.api.pii.model;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TemporalSourceTest {

	@Test
	void validate_TemporalSource_JsonCreator_creates_correct_TemporalSource_with_AudioType() {

		TemporalSource temporalSource = new TemporalSource(1L, 2L, TemporalCorpusType.AUDIO, Reason.BIOMETRIC_DATA,
				Violation.CCPA, LocalDateTime.MAX, LocalDateTime.MIN);
		TemporalSource targetSource = new TemporalSource(1L, 2L, CorpusType.AUDIO, Reason.BIOMETRIC_DATA,
				Violation.CCPA, LocalDateTime.MAX, LocalDateTime.MIN);

		assertThat(temporalSource, is(targetSource));
	}

	@Test
	void validate_TemporalSource_JsonCreator_creates_correct_TemporalSource_with_VideoType() {

		TemporalSource temporalSource = new TemporalSource(1L, 2L, TemporalCorpusType.VIDEO, Reason.BIOMETRIC_DATA,
				Violation.CCPA, LocalDateTime.MAX, LocalDateTime.MIN);
		TemporalSource targetSource = new TemporalSource(1L, 2L, CorpusType.VIDEO, Reason.BIOMETRIC_DATA,
				Violation.CCPA, LocalDateTime.MAX, LocalDateTime.MIN);

		assertThat(temporalSource, is(targetSource));
	}

	@Test
	void validate_TemporalSource_JsonCreator_creates_correct_TemporalSource_with_AnimType() {

		TemporalSource temporalSource = new TemporalSource(1L, 2L, TemporalCorpusType.ANIMATION, Reason.BIOMETRIC_DATA,
				Violation.CCPA, LocalDateTime.MAX, LocalDateTime.MIN);
		TemporalSource targetSource = new TemporalSource(1L, 2L, CorpusType.ANIMATION, Reason.BIOMETRIC_DATA,
				Violation.CCPA, LocalDateTime.MAX, LocalDateTime.MIN);

		assertThat(temporalSource, is(targetSource));
	}

}
