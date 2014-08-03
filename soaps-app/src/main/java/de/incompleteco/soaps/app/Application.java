package de.incompleteco.soaps.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.incompleteco.soaps.app.redis.Utilities;

/**
 * application bootstrap
 * @author wschipp
 *
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class,args);
	}

	@Bean
	public Utilities utilities() {
		return new Utilities();
	}
}
