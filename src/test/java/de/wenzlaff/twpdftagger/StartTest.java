package de.wenzlaff.twpdftagger;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 
 * @author Thomas Wenzlaff
 *
 */
public class StartTest {

	@ParameterizedTest(name = "{index}. Überprüfter Kommandozeilen Parameter: {arguments}")
	@CsvSource({ "-h", "-v", "-Kommandozeilenfehler" })
	void commandoZeilenTest(String kommandozeilenparameter, TestInfo info) throws Exception {

		System.out.println("----------------------------------------------------------------------------------> "
				+ info.getDisplayName() + " <-------------------------------------");

		Start.main(new String[] { kommandozeilenparameter });
	}

}
