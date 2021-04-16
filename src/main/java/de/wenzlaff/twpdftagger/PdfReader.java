package de.wenzlaff.twpdftagger;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Lesen von PDF Dokumenten.
 * 
 * @author Thomas Wenzlaff
 *
 */
final public class PdfReader {

	private static final Logger LOG = LogManager.getLogger(PdfReader.class);

	private static Options options = new Options();

	private PdfReader() {
		// nur Util
	}

	public static void main(String[] args) {

		options.addOption("h", "hilfe", false, "zeige die Hilfe.");
		options.addOption("v", "version", false, "zeige die Version des Programms an");

		Option pdfInputVerzeichnis = OptionBuilder.withArgName("PDF Datei").hasArg().withDescription("Pdf-Input Datei inkl. Path)").create("i");
		options.addOption(pdfInputVerzeichnis);

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (Exception e) {
			System.err.println("ERROR: Fehler beim parsen der Kommandozeile. " + e.getLocalizedMessage());
			ausgabeHilfe();
			return;
		}
		if (cmd.hasOption("h")) {
			ausgabeHilfe();
			return;
		}
		if (cmd.hasOption("v")) {
			System.out.println("Programm Version " + Start.VERSION);
			ausgabeHilfe();
			return;
		}
		String inputVerz = "input";

		if (cmd.hasOption("i")) {
			inputVerz = cmd.getOptionValue("i");
			System.out.println("PDF Input Datei:" + inputVerz);
		}

		String trenner = ",";

		if (cmd.hasOption("t")) {
			trenner = cmd.getOptionValue("t");
			System.out.println("Trenner:" + trenner);
		}

		LOG.info("Programm Start mit Version " + Start.VERSION + " ...");

		String erg = PdfExtracter.getTextMitTrenner(new File(inputVerz), trenner);
		LOG.info("");
		LOG.info("Textinhalt:");
		LOG.info("");
		LOG.info(erg);
		LOG.info("");
	}

	private static void ausgabeHilfe() {

		HelpFormatter formatter = new HelpFormatter();

		formatter.printHelp("de.wenzlaff.twpdfextraktor", options);

		System.out.println();
		System.out.println("Siehe http://www.wenzlaff.info");
	}
}