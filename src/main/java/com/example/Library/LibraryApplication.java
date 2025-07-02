package com.example.Library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Основной класс приложения библиотеки.
 * @see SpringBootApplication
 * @see EnableScheduling
 */
@SpringBootApplication
@EnableScheduling
public class LibraryApplication {

	/** Точка входа в приложение.
	 * @param args аргументы командной строки */
	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

}
