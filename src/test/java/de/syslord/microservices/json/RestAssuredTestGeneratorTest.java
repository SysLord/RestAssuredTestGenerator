package de.syslord.microservices.json;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestAssuredTestGeneratorTest {

	private static class TestJsonList {

		@JsonProperty
		private List<TestJson> values = Arrays.asList(new TestJson(), new TestJson());

	}

	private static class TestJson {

		@JsonProperty
		private String valueA = "a";

		@JsonProperty
		private boolean valueB = true;

	}

	@Test
	public void testGenerate() throws Exception {

		String json = convertToJsonString(new TestJson());

		String result = RestAssuredTestGenerator.generateRestAssuredTest(
				json,
				TestJson.class,
				"");

		String[] lines = result.split("\n");
		assertEquals("assertEquals(\"a\", jsonPath.get(\"valueA\"));", lines[0]);
		assertEquals("assertEquals(true, jsonPath.get(\"valueB\"));", lines[1]);
	}

	@Test
	public void testGenerateFromList() throws Exception {

		String json = convertToJsonString(new TestJsonList());

		String result = RestAssuredTestGenerator.generateRestAssuredTest(
				json,
				TestJson.class,
				"values[0].");

		String[] lines = result.split("\n");
		assertEquals("assertEquals(\"a\", jsonPath.get(\"values[0].valueA\"));", lines[0]);
		assertEquals("assertEquals(true, jsonPath.get(\"values[0].valueB\"));", lines[1]);
	}

	private static String convertToJsonString(Object object) {
		try {
			return new ObjectMapper()
				.writerWithDefaultPrettyPrinter()
				.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
