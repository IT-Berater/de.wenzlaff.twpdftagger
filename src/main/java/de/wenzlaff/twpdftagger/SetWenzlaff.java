package de.wenzlaff.twpdftagger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.xml.xmp.XmpWriter;

/**
 * Klasse zum setzen der eigenen Metadaten in PDF Dateien.
 * 
 * Die zu setzenden Metainfos werden aus einer Propertie Datei eingelesen.
 * 
 * @author Thomas Wenzlaff
 *
 */
final public class SetWenzlaff {

	private static final String THOMAS_WENZLAFF = "Thomas Wenzlaff";
	private static final Logger LOG = LogManager.getLogger(SetWenzlaff.class);

	/**
	 * Setzte die Metadaten in eine PDF Datei.
	 * 
	 * @param pdfInputDateiName
	 * @param pdfZielPath
	 * @param pdfZielDateiName
	 * @param passwort
	 * @throws IOException
	 * @throws DocumentException
	 * @throws FileNotFoundException
	 */
	public static void setMetadaten(URL pdfInputDateiName, Path pdfZielPath, String pdfZielDateiName, String passwort)
			throws IOException, DocumentException, FileNotFoundException {

		PdfReader inputPdf = new PdfReader(pdfInputDateiName);

		LOG.info("Überschreibe die Metadaten aus Datei {} mit eigene.", pdfInputDateiName);
		LOG.info("Folgende Metadaten werden überschrieben: {} ", inputPdf.getInfo());

		File ausgabePdf = new File(pdfZielPath.toFile(), pdfZielDateiName);

		PdfStamper outputPdf = new PdfStamper(inputPdf, new FileOutputStream(ausgabePdf));

		HashMap<String, String> metadata = inputPdf.getInfo();
		setMetadatenWenzlaff(metadata, pdfZielDateiName);

		outputPdf.setMoreInfo(metadata);
		outputPdf.setFullCompression();

		if (!passwort.isEmpty()) {
			LOG.info("Verschlüsselung für User Thomas Wenzlaff");
			outputPdf.setEncryption(passwort.getBytes(), THOMAS_WENZLAFF.getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.STANDARD_ENCRYPTION_128);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XmpWriter xmp = new XmpWriter(baos, metadata);
		xmp.close();
		outputPdf.setXmpMetadata(baos.toByteArray());
		outputPdf.close();
		inputPdf.close();
	}

	private static void setMetadatenWenzlaff(HashMap<String, String> metadata, String pdfZielDateiName) {

		// exiftool -artist="Thomas Wenzlaff" -copyright="© Copyright 2020 Thomas
		// Wenzlaff, all rights reserved - info-anfrage@wenzlaff.de" -contact="Thomas
		// Wenzlaff" -writer="Thomas Wenzlaff" -country="Deutschland"
		// -city="Langenhagen" -DateCreated="20200904" -credit="Thomas Wenzlaff"
		// -creditLine="Thomas Wenzlaff" -creator="Thomas Wenzlaff"
		// -licensorURL="http://kleinhirn.eu/impressum/" -copyrightNotice="© Copyright
		// 2020 Thomas Wenzlaff, all rights reserved - info-anfrage@wenzlaff.de" -s
		// ./uploads/2010/*

		metadata.put("Title", pdfZielDateiName);
		metadata.put("Subject", pdfZielDateiName);
		metadata.put("Author", THOMAS_WENZLAFF);
		metadata.put("artist", THOMAS_WENZLAFF);
		metadata.put("contact", THOMAS_WENZLAFF);
		metadata.put("credit", THOMAS_WENZLAFF);
		metadata.put("creditLine", THOMAS_WENZLAFF);
		metadata.put("creator", THOMAS_WENZLAFF);
		metadata.put("writer", THOMAS_WENZLAFF);

		metadata.put("country", "Deutschland");
		metadata.put("city", "Langenhagen");

		metadata.put("DateCreated", getDatum());

		metadata.put("licensorURL", "http://kleinhirn.eu/impressum/");

		String copyrightNotice = "© Copyright " + getJahr() + " " + THOMAS_WENZLAFF + ", all rights reserved - info-anfrage@wenzlaff.de";
		metadata.put("copyrightNotice", copyrightNotice);
		metadata.put("copyright", copyrightNotice);

		metadata.put("Keywords", pdfZielDateiName);

		metadata.put("Creator", "© " + getJahr() + " de.wenzlaff.twpdftagger " + Start.VERSION + " by " + THOMAS_WENZLAFF);
		metadata.put("Copyright", THOMAS_WENZLAFF);
		metadata.put("Webpage", "http://www.wenzlaff.de");

		metadata.put("Dateiname", pdfZielDateiName);
	}

	private static String getJahr() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("YYYY"));
	}

	private static String getDatum() {
		return LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
	}
}