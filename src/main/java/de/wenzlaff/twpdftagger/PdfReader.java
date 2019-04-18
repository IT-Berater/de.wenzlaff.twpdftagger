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

	public static String getText(File pdfInput, String trenner) {

		String pdfFileInText = "";
		try {
			PDFTextStripper textStripper = new PDFTextStripper();
			textStripper.setStartPage(1);
			textStripper.setEndPage(3);

			PDDocument document = PDDocument.load(pdfInput);

			if (!document.isEncrypted()) {

				pdfFileInText = textStripper.getText(document);

				pdfFileInText = pdfFileInText.replace("\n", trenner);
			} else {
				String meldung = "Info: Das Dokument ist verschlüsselt und kann nicht gelesen werden.";
				System.out.println(meldung);
				return meldung;
			}
		} catch (IOException e) {
			return "Fehler: " + e.getMessage();

		}
		// letztes Trennzeichen entfernen
		pdfFileInText = pdfFileInText.substring(0, pdfFileInText.length() - trenner.length());
		return pdfFileInText;
	}
}
