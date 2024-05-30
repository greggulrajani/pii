package com.humannative.api.pii.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TextSourceTest {

	@Test
	void validate_TextSource_JsonCreator_creates_correct_TextSource_with_TextType() {

		TextSource textSource = new TextSource(1L, 2L, Reason.BIOMETRIC_DATA, Violation.CCPA, 10L, 20L);

		assertThat(textSource.corpusType(), is(CorpusType.TEXT));
	}

}
