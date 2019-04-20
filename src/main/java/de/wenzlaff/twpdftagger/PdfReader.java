package de.wenzlaff.twpdftagger;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * 
 * @author Thomas Wenzlaff
 *
 */
final public class PdfReader {

	private PdfReader() {
		// nur Util
	}

	public static String getTextMitTrenner(File pdfInput, String trenner) {

		String pdfText = "";
		try {
			PDDocument document = PDDocument.load(pdfInput);

			if (!document.isEncrypted()) {
				PDFTextStripper textStripper = new PDFTextStripper();
				pdfText = textStripper.getText(document);
				pdfText = pdfText.replace("\n", trenner);
			} else {
				String meldung = "Info: Das Dokument ist verschlüsselt und kann nicht gelesen werden.";
				System.out.println(meldung);
				return meldung;
			}
		} catch (IOException e) {
			return "Fehler: " + e.getMessage();

		}
		// letztes Trennzeichen entfernen
		pdfText = pdfText.substring(0, pdfText.length() - trenner.length());
		return pdfText;
	}
}
