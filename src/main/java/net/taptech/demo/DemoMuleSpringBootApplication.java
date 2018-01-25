package net.taptech.demo;

import net.taptech.autoconfiguration.EnableMuleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@EnableMuleConfiguration
@SpringBootApplication
public class DemoMuleSpringBootApplication {

	private static final Logger logger = LoggerFactory.getLogger(DemoMuleSpringBootApplication.class);

	@Autowired
	private ApplicationContext context;

	public static void main(String... args) {
		logger.info("Starting SpringApplication...");
		SpringApplication app = new SpringApplication(DemoMuleSpringBootApplication.class);
		app.setBannerMode(Banner.Mode.CONSOLE);
		app.setWebEnvironment(false);
		app.run();
		logger.info("SpringApplication has started...");
	}
}
