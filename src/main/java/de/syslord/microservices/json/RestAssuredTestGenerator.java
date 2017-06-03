package de.syslord.microservices.json;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import io.restassured.path.json.JsonPath;

public class RestAssuredTestGenerator {

	public static String generateRestAssuredTest(String json, Class<?> clazz, String accessor) {
		List<String> properties = Lists.newArrayList();
		ReflectionUtils.doWithFields(
				clazz,
				field -> {
					field.setAccessible(true);

					properties.add(getNameOrJsonPropertyName(field));
				},
				field -> AnnotationUtils.getAnnotation(field, JsonProperty.class) != null);

		JsonPath jsonPath = JsonPath.from(json);

		return properties.stream()
			.map(property -> getTestLine(jsonPath, property, accessor))
			.collect(Collectors.joining("\n"));
	}

	private static String getNameOrJsonPropertyName(Field field) {
		JsonProperty[] annotationsByType = field.getAnnotationsByType(JsonProperty.class);
		if (annotationsByType.length > 0) {
			JsonProperty p = annotationsByType[0];
			if (p.value() != null && !p.value().isEmpty()) {
				return p.value();
			}
		}
		return field.getName();
	}

	private static String getTestLine(JsonPath jsonPath, String property, String accessor) {
		try {
			String stringValue = jsonPath.get(accessor + property);
			stringValue = stringValue.replace("\n", "\\n");
			return String.format("assertEquals(\"%s\", jsonPath.get(\"%s%s\"));", stringValue, accessor, property);
		} catch (ClassCastException e) {
			// ignore
		}

		try {
			Object objectValue = jsonPath.get(accessor + property);
			String stringValue = String.valueOf(objectValue);
			return String.format("assertEquals(%s, jsonPath.get(\"%s%s\"));", stringValue, accessor, property);
		} catch (ClassCastException e) {
			// ignore
		}

		return String.format("assertEquals(\"??\", jsonPath.get(\"%s%s\"));", accessor, property);
	}
}
