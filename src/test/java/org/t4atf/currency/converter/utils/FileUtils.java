package org.t4atf.currency.converter.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtils {

	public static String readFileAsString(String filePath) throws IOException, URISyntaxException {
		return new String(Files.readAllBytes(
			Paths.get(FileUtils.class.getClassLoader().getResource(filePath).toURI())));
	}
}
