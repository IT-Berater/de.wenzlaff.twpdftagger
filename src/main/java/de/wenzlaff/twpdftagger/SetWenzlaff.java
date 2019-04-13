package de.wenzlaff.twpdftagger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Properties;

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

	private static final String PROP_DATEINAME = "twpdftagger-metadata.properties";

	private static final Logger LOG = LogManager.getLogger(SetWenzlaff.class);

	private static Properties appProps;

	/**
	 * Setzte die Metadaten in eine PDF Datei.
	 * 
	 * @param pdfInputDateiName
	 * @param pdfZielPath
	 * @param pdfZielDateiName
	 * @throws IOException
	 * @throws DocumentException
	 * @throws FileNotFoundException
	 */
	public static void setMetadaten(URL pdfInputDateiName, Path pdfZielPath, String pdfZielDateiName)
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

		if (appProps.getProperty("Verschlüsseln").equalsIgnoreCase("ja")) {
			LOG.info("Verschlüsselung für User {} ", appProps.getProperty("Username"));
			outputPdf.setEncryption(appProps.getProperty("Passwort").getBytes(),
					appProps.getProperty("Username").getBytes(), PdfWriter.ALLOW_PRINTING,
					PdfWriter.STANDARD_ENCRYPTION_128);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XmpWriter xmp = new XmpWriter(baos, metadata);
		xmp.close();
		outputPdf.setXmpMetadata(baos.toByteArray());
		outputPdf.close();
	}

	private static void setMetadatenWenzlaff(HashMap<String, String> metadata, String pdfZielDateiName) {

		Properties appProps = readMetadata();

		metadata.put("Title", appProps.getProperty("Title"));
		metadata.put("Subject", appProps.getProperty("Subject"));
		metadata.put("Author", appProps.getProperty("Author"));
		metadata.put("Keywords", appProps.getProperty("Keywords") + ", " + pdfZielDateiName);
		// Anwendung
		metadata.put("Creator", "(c) de.wenzlaff.twpdftagger by Thomas Wenzlaff");

		// Benuzterdefinierte
		metadata.put("Copyright", appProps.getProperty("Copyright"));
		metadata.put("Webpage", appProps.getProperty("Webpage"));
		metadata.put("Dateiname", pdfZielDateiName);
	}

	private static Properties readMetadata() {

		try {
			String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			String appConfigPath = rootPath + PROP_DATEINAME;

			appProps = new Properties();
			appProps.load(new FileInputStream(appConfigPath));

			LOG.info("Lese Metadata aus Properties Datei. {}", appProps);

		} catch (IOException e) {
			LOG.error("Fehler beim lesen der {} Properties.", PROP_DATEINAME, e);
		}
		return appProps;
	}

}
