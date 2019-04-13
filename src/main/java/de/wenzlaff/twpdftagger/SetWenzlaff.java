package de.wenzlaff.twpdftagger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
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
public class SetWenzlaff {

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
			outputPdf.setEncryption(passwort.getBytes(), "Thomas Wenzlaff".getBytes(), PdfWriter.ALLOW_PRINTING,
					PdfWriter.STANDARD_ENCRYPTION_128);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XmpWriter xmp = new XmpWriter(baos, metadata);
		xmp.close();
		outputPdf.setXmpMetadata(baos.toByteArray());
		outputPdf.close();
	}

	private static void setMetadatenWenzlaff(HashMap<String, String> metadata, String pdfZielDateiName) {

		metadata.put("Title", pdfZielDateiName);
		metadata.put("Subject", pdfZielDateiName);
		metadata.put("Author", "Thomas Wenzlaff");
		metadata.put("Keywords", pdfZielDateiName);

		// Anwendung
		metadata.put("Creator", "(c) 2019 de.wenzlaff.twpdftagger by Thomas Wenzlaff");

		// Benuzterdefinierte
		metadata.put("Copyright", "Thomas Wenzlaff");
		metadata.put("Webpage", "http://www.wenzlaff.de");
		metadata.put("Dateiname", pdfZielDateiName);
	}

}
