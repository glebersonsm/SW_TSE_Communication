package com.sw.tse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;


@EnableFeignClients
@SpringBootApplication
public class SwTseCommunicationApplication {

	public static void main(String[] args) {
		configurarLogPath();
		SpringApplication.run(SwTseCommunicationApplication.class, args);
	}

	/**
	 * Define o diretório dos logs como {@code logs/} dentro da pasta do JAR (ou user.dir quando não houver JAR).
	 * Sobrescreve-se via variável de ambiente LOGGING_FILE_PATH.
	 */
	private static void configurarLogPath() {
		if (System.getProperty("sw.tse.logging.path") != null) {
			return;
		}
		String envPath = System.getenv("LOGGING_FILE_PATH");
		if (envPath != null && !envPath.isBlank()) {
			System.setProperty("sw.tse.logging.path", envPath.trim());
			return;
		}
		File baseDir;
		try {
			URI uri = SwTseCommunicationApplication.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI();
			if ("jar".equals(uri.getScheme())) {
				String s = uri.getRawSchemeSpecificPart();
				int exclamation = s.indexOf('!');
				String jarPath = (exclamation > 0) ? s.substring(0, exclamation) : s;
				Path p = Paths.get(new URI(jarPath));
				baseDir = p.toFile().getParentFile();
			} else {
				File codeSource = Paths.get(uri).toFile();
				baseDir = codeSource.isFile() ? codeSource.getParentFile() : codeSource.getParentFile();
			}
			if (baseDir == null) {
				baseDir = new File(System.getProperty("user.dir"));
			}
		} catch (Exception e) {
			baseDir = new File(System.getProperty("user.dir"));
		}
		String logPath = new File(baseDir, "logs").getAbsolutePath();
		System.setProperty("sw.tse.logging.path", logPath);
		System.setProperty("sw.tse.logging.base-dir", baseDir.getAbsolutePath());
	}
}
	