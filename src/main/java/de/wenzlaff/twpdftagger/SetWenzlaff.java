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
import com.itextpdf.text.xml.xmp.XmpWriter;

/**
 * Klasse zum setzen der eigenen Metadaten in PDF Dateien.
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
	 * @param outputPath
	 * @param pdfDateiName
	 * @throws IOException
	 * @throws DocumentException
	 * @throws FileNotFoundException
	 */
	public static void setMetadaten(URL pdfInputDateiName, Path outputPath, String pdfDateiName) throws IOException, DocumentException, FileNotFoundException {

		PdfReader inputPdf = new PdfReader(pdfInputDateiName);

		LOG.info("Überschreibe die Metadaten aus Datei {} mit eigene.", pdfInputDateiName);
		LOG.info("Folgende Metadaten werden überschrieben: {} ", inputPdf.getInfo());

		File ausgabePdf = new File(outputPath.toFile(), pdfDateiName);

		PdfStamper outputPdf = new PdfStamper(inputPdf, new FileOutputStream(ausgabePdf));

		HashMap<String, String> metadata = inputPdf.getInfo();
		setMetadatenWenzlaff(metadata, pdfDateiName);

		outputPdf.setMoreInfo(metadata);
		outputPdf.setFullCompression();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XmpWriter xmp = new XmpWriter(baos, metadata);
		xmp.close();
		outputPdf.setXmpMetadata(baos.toByteArray());
		outputPdf.close();
	}

	private static void setMetadatenWenzlaff(HashMap<String, String> metadata, String pdfDateiName) {
		metadata.put("Title", "Thomas Wenzlaff");
		metadata.put("Subject", "(c) 2018 by Thomas Wenzlaff");
		metadata.put("Author", "Thomas Wenzlaff");
		metadata.put("Keywords", "TWPdfTagger, Thomas Wenzlaff, www.wenzlaff.de, " + pdfDateiName);
		// Anwendung
		metadata.put("Creator", "(c) de.wenzlaff.twpdftagger");

		// Benuzterdefinierte
		metadata.put("Copyright", "Thomas Wenzlaff");
		metadata.put("Webpage", "http://www.wenzlaff.de");
		metadata.put("Dateiname", pdfDateiName);
	}

}
