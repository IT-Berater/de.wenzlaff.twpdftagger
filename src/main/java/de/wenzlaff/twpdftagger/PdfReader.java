package de.wenzlaff.twpdftagger;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Lesen von PDF Dokumenten.
 * 
 * @author Thomas Wenzlaff
 *
 */
final public class PdfReader {

	private static final Logger LOG = LogManager.getLogger(PdfReader.class);

	private PdfReader() {
		// nur Util
	}

	public static String getTextMitTrenner(File pdfInput, String trenner) {
		if (trenner == null) {
			trenner = ",";
		}

		String pdfText = "";
		try {
			PDDocument document = PDDocument.load(pdfInput);

			if (!document.isEncrypted()) {
				PDFTextStripper textStripper = new PDFTextStripper();
				pdfText = textStripper.getText(document);
				pdfText = pdfText.replace("\n", trenner);
			} else {
				String meldung = "Info: Das Dokument ist verschlüsselt und kann nicht gelesen werden.";
				LOG.info("{}", meldung);
				return meldung;
			}
		} catch (IOException e) {
			String fehler = "Fehler: " + e.getMessage();
			LOG.error("{}", fehler);
			return fehler;

		}
		// letztes Trennzeichen entfernen
		pdfText = pdfText.substring(0, pdfText.length() - trenner.length());
		return pdfText;
	}
}
