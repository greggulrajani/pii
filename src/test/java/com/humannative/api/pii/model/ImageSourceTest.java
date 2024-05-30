package com.humannative.api.pii.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ImageSourceTest {

	@Test
	void validate_ImageSource_JsonCreator_creates_correct_ImageSource_with_ImageType() {

		ImageSource imageSource = new ImageSource(1L, 2L, Reason.BIOMETRIC_DATA, Violation.CCPA, 10L, 20L, 40L, 50L);

		assertThat(imageSource.corpusType(), is(CorpusType.IMAGE));
	}

}
