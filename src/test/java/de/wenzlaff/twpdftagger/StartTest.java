package de.wenzlaff.twpdftagger;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

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

	@ParameterizedTest(name = "{index}. Überprüfter Kommandozeilen Parameter: {arguments}")
	@CsvSource({ "-h", "-v", "-Kommandozeilenfehler" })
	void commandoZeilenTest(String kommandozeilenparameter, TestInfo info) throws Exception {

		System.out.println("----------------------------------------------------------------------------------> " + info.getDisplayName()
				+ " <-------------------------------------");

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

			assertEquals("Fehler:  (No such file or directory)", ergebnisText);
		}

		@DisplayName("PDF extrahieren von Text mit Ungültigen Datei gleich null")
		@Test
		void testPdfTextExportNegNull() {

			String ergebnisText = PdfExtracter.getTextMitTrenner(null, ",");

			System.out.println("Ergebnis: " + ergebnisText);

			assertEquals("Fehler: Keine PDF Datei angegeben", ergebnisText);
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

			assertEquals(
					"12 Top ,Skills,Emotionale Intelligenz,Kritisches Denken,Veränderungsbereitschaft,Analytisches Denken,Selbstorganisation,Neugier,Selektion relevanter Informationen,Problemlösungskompetenz,Interkulturelle Kompetenz,Entwicklungsbereitschaft,Digitale Kommunikation,Lebenslanges Lernen,Dr. Kleinhirn.eu",
					ergebnisText);
		}

		@DisplayName("PDF extrahieren von Text mit defalut Trenner")
		@Test
		void testPdfTextExportTrenner() {

			String ergebnisText = PdfExtracter.getTextMitTrenner(new File("src/test/resources/Top-Skills.pdf"), null);

			System.out.println("Ergebnis: " + ergebnisText);

			assertEquals(
					"12 Top ,Skills,Emotionale Intelligenz,Kritisches Denken,Veränderungsbereitschaft,Analytisches Denken,Selbstorganisation,Neugier,Selektion relevanter Informationen,Problemlösungskompetenz,Interkulturelle Kompetenz,Entwicklungsbereitschaft,Digitale Kommunikation,Lebenslanges Lernen,Dr. Kleinhirn.eu",
					ergebnisText);
		}
	}
}