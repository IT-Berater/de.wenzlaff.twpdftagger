package de.wenzlaff.twpdftagger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itextpdf.text.DocumentException;

final public class Start {

	private static final Logger LOG = LogManager.getLogger(Start.class.getName());

	public static final String VERSION = "0.0.9";

	private static Options options = new Options();

	/**
	 * Start Methode.
	 * 
	 * @param args
	 * @throws Exception
	 */

	@SuppressWarnings({ "deprecation", "static-access" })
	public static void main(String[] args) throws Exception {

		options.addOption("h", "hilfe", false, "zeige die Hilfe.");
		options.addOption("v", "version", false, "zeige die Version des Programms an");

		Option pdfInputVerzeichnis = OptionBuilder.withArgName("Input Verzeichnis").hasArg().withDescription("Pdf-Input Verzeichnis relativ zum Userverzeichnis (default ./input)")
				.create("i");
		options.addOption(pdfInputVerzeichnis);

		Option pdfOutputVerzeichnis = OptionBuilder.withArgName("Output Verzeichnis").hasArg()
				.withDescription("Pdf-Output Verzeichnis  relativ zum Userverzeichnis (default ./output)").create("o");
		options.addOption(pdfOutputVerzeichnis);

		Option pdfInputDatei = OptionBuilder.withArgName("Input PDF Datei").hasArg().withDescription("Pdf-Input Datei").create("d");
		options.addOption(pdfInputDatei);

		Option passwortOption = OptionBuilder.withArgName("Passwort").hasArg()
				.withDescription("Optional, Passwort dann wird das PDF mit diesem verschlüsselt (default ohne Passwort)").create("p");
		options.addOption(passwortOption);

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
			System.out.println("Programm Version " + VERSION);
			ausgabeHilfe();
			return;
		}
		String inputVerz = "input";

		if (cmd.hasOption("i")) {
			inputVerz = cmd.getOptionValue("i");
			System.out.println("Input:" + inputVerz);
		}

		String outputVerz = "output";

		if (cmd.hasOption("o")) {
			outputVerz = cmd.getOptionValue("o");
			System.out.println("Output:" + outputVerz);
		}

		String passwort = "";

		if (cmd.hasOption("p")) {
			passwort = cmd.getOptionValue("p");
			System.out.println("Passwort:" + passwort);
		}

		String inputDatei = "";

		if (cmd.hasOption("d")) { // TODO: so implementieren, das ein doppelklick startet
			inputDatei = cmd.getOptionValue("d");
			System.out.println("Input PDF Datei:" + inputDatei);
		}
		LOG.info("Programm Start mit Version " + VERSION + " ...");

		String rootVerzeichnis = System.getProperty("user.dir");
		Path inputPath = Paths.get(rootVerzeichnis, inputVerz);
		Path outputPath = Paths.get(rootVerzeichnis, outputVerz);
		mkdir(inputPath);
		mkdir(outputPath);

		LOG.info("Überwache jetzt das Input Verzeichnis: {} ", inputPath);

		WatchService watchService = FileSystems.getDefault().newWatchService();
		inputPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

		WatchKey key;
		while ((key = watchService.take()) != null) {
			for (WatchEvent<?> event : key.pollEvents()) {
				try {
					aktion(inputPath, outputPath, event, passwort);
				} catch (Exception e) {
					LOG.error("Fehler aufgetreten: {}", e.getLocalizedMessage());
				}
			}
			key.reset();
		}
		LOG.info("Programm beendet.");
	}

	private static void ausgabeHilfe() {

		HelpFormatter formatter = new HelpFormatter();

		formatter.printHelp("de.wenzlaff.twpdftagger", options);

		System.out.println();
		System.out.println("Siehe http://www.wenzlaff.info");
	}

	private static void aktion(final Path inputPath, final Path outputPath, WatchEvent<?> event, String passwort)
			throws FileNotFoundException, IOException, DocumentException, URISyntaxException {

		String neueDatei = event.context().toString();

		if (neueDatei.endsWith(".pdf")) { // nur PDF Dateien bearbeiten

			LOG.info("Bearbeite neue PDF Datei: {} ", neueDatei);

			URL inputUrl = null;

			inputUrl = new URL("file://" + inputPath.toString() + "/" + neueDatei);
			SetWenzlaff.setMetadaten(inputUrl, outputPath, neueDatei, passwort);

			LOG.info("");
			LOG.info("{}", PdfExtracter.getTextMitTrenner(new File(inputUrl.toURI()), ","));
		}
	}

	private static void mkdir(Path path) {

		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				LOG.error("Fehler beim erzeugen des Verzeichnis: {}", e);
			}
		}
	}
}
