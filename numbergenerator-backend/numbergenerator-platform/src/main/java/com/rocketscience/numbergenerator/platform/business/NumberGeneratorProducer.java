package com.rocketscience.numbergenerator.platform.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.rocketscience.numbergenerator.lib.NumberGenerator;

@EnableScheduling
@Service
public class NumberGeneratorProducer {
	
	@Autowired
	private NumberGenerator numberGenerator;
	
	@Autowired(required=false)
	KafkaTemplate<Object, Long> template;
	
	@Scheduled(fixedDelay = 1000)
	private void send() {
		try {
			template.send("numbers", numberGenerator.random());
		} catch (Exception e) {
			//todo: manage exception
		}
	}
}
