package de.wenzlaff.twpdftagger;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Start {

	private static final Logger LOG = LogManager.getLogger(Start.class);

	/**
	 * Start Methdode.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		LOG.info("Programm Start ... ");

		final String rootVerzeichnis = System.getProperty("user.dir");
		final Path inputPath = Paths.get(rootVerzeichnis, "input");
		final Path outputPath = Paths.get(rootVerzeichnis, "output");
		mkdir(inputPath);
		mkdir(outputPath);

		LOG.info("Überwache jetzt das Input Verzeichnis: {} ", inputPath);

		WatchService watchService = FileSystems.getDefault().newWatchService();
		inputPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE); // nur neue Dateien erkennen

		WatchKey key;
		while ((key = watchService.take()) != null) {
			for (WatchEvent<?> event : key.pollEvents()) {
				aktion(inputPath, outputPath, event);
			}
			key.reset();
		}
		LOG.info("Programm beendet.");
	}

	private static void aktion(final Path inputPath, final Path outputPath, WatchEvent<?> event) {

		String neueDatei = event.context().toString();

		if (neueDatei.endsWith(".pdf")) { // nur PDF Dateien bearbeiten

			LOG.info("Bearbeite neue PDF Datei: {} ", neueDatei);

			URL inputUrl = null;
			try {
				inputUrl = new URL("file://" + inputPath.toString() + "/" + neueDatei);
				SetWenzlaff.setMetadaten(inputUrl, outputPath, neueDatei);
			} catch (Exception e) {
				LOG.error("Nicht behandelte Datei: {}  Input URL: {} Fehlermeldung: {}", neueDatei, inputUrl, e);
			}
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
