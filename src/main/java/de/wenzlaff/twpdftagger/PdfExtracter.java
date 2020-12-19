package de.wenzlaff.twpdftagger;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * PDF Extracter mit Apache PDFBox.
 * 
 * @author Thomas Wenzlaff
 */
public class PdfExtracter {

	private static final Logger LOG = LogManager.getLogger(PdfExtracter.class);

	/**
	 * Liefet im Log den Text des PDFs via Apache PDFBox.
	 * 
	 * @param pdfInput
	 * @param trenner
	 * @return den Inhalt des PDFs separiert durch trenner
	 */
	public static String getTextMitTrenner(File pdfInput, String trenner) {
		if (pdfInput == null) {
			return "Fehler: Keine PDF Datei angegeben";
		}
		if (trenner == null) {
			trenner = ",";
		}
		LOG.info("Verwende Trennzeichen {}", trenner);

		String pdfText = "";
		try {
			PDDocument document = PDDocument.load(pdfInput, MemoryUsageSetting.setupTempFileOnly());

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
