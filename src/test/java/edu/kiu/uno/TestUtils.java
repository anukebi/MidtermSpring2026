package edu.kiu.uno;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {

	public static void check(String message, boolean condition) {
		assertThat(condition).withFailMessage(message).isTrue();
	}

}
