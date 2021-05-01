package com.rocketscience.numbergenerator.platform;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import com.rocketscience.numbergenerator.lib.NumberGenerator;

@SpringBootApplication
public class NumberGeneratorPlatformApplication {
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public NumberGenerator getNumberGenerator() {
		return new NumberGenerator();
	}

	public static void main(String[] args) {
		SpringApplication.run(NumberGeneratorPlatformApplication.class, args);
	}

}
