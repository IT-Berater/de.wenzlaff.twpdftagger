package de.wenzlaff.twpdftagger;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Testklasse für PDF.
 * 
 * @author Thomas Wenzlaff
 */
class StartTest {

	@BeforeAll
	static void setup() {
		BasicConfigurator.configure();

	}

	@Disabled
	@ParameterizedTest(name = "{index}. Überprüfter Kommandozeilen Parameter mit einer PDF-Input Datei: {arguments}")
	@CsvSource({ "-d src/test/resources/Top-Skills.pdf" })
	void commandoZeilenTestEineDatei(String kommandozeilenparameter, TestInfo info) throws Exception {

		printTrenner(info);

		Start.main(new String[] { kommandozeilenparameter });
	}

	@ParameterizedTest(name = "{index}. Überprüfter Kommandozeilen Parameter: {arguments}")
	@CsvSource({ "-h", "-v", "-Kommandozeilenfehler" })
	void commandoZeilenTest(String kommandozeilenparameter, TestInfo info) throws Exception {

		printTrenner(info);

		Start.main(new String[] { kommandozeilenparameter });
	}

	@DisplayName("Neg. extrahieren Tests")
	@Nested
	class NegativTests {

		@DisplayName("PDF extrahieren von Text mit Ungültigen Verzeichnis")
		@Test
		void testPdfTextExportNeg() {

			String ergebnisText = PdfExtracter.getTextMitTrenner(new File(""), ",");

			System.out.println("Ergebnis: " + ergebnisText);
		}

		@DisplayName("PDF extrahieren von Text mit Ungültigen Datei gleich null")
		@Test
		void testPdfTextExportNegNull() {

			String ergebnisText = PdfExtracter.getTextMitTrenner(null, ",");

			System.out.println("Ergebnis: " + ergebnisText);
		}
	}

	@DisplayName("Pos. extrahieren Tests")
	@Nested
	class PositivTests {

		@DisplayName("PDF extrahieren von Text")
		@Test
		void testPdfTextExport() {

			String ergebnisText = PdfExtracter.getTextMitTrenner(new File("src/test/resources/Top-Skills.pdf"), ",");

			System.out.println("Ergebnis: " + ergebnisText);

			assertTrue(ergebnisText.length() > 10);
		}

		@DisplayName("PDF extrahieren von Text mit defalut Trenner")
		@Test
		void testPdfTextExportTrenner() {

			String ergebnisText = PdfExtracter.getTextMitTrenner(new File("src/test/resources/Top-Skills.pdf"), null);

			System.out.println("Ergebnis: " + ergebnisText);

			assertTrue(ergebnisText.length() > 10);
		}
	}

	private void printTrenner(TestInfo info) {
		System.out.println("----------------------------------------------------------------------------------> " + info.getDisplayName()
				+ " <-------------------------------------");
	}
}