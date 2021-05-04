package com.rocketscience.numbergenerator.viewer.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@Service
public class NumberGeneratorViewerService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${numberGenerator.rest.endpoint}")
	private String numberGeneratorEndpoint;
	
	@Autowired
    private SimpMessagingTemplate template;

	@Getter
	private List<Long> numbers = new ArrayList<>();

	private long counter = 1;
	
	@Recover
	public List<Long> generateAndGetNumbersFallback(RuntimeException re) {
		numbers.add(counter <= 100 ? counter++ : 1);
		return numbers;
	}
	
	@Retryable(recover = "generateAndGetNumbersFallback", value = java.net.ConnectException.class, maxAttempts = 1)
	public List<Long> generateAndGetNumbers() {
		long result = restTemplate.getForObject(numberGeneratorEndpoint, Long.class);
		
		numbers.add(result);
		return numbers;
	}

	@KafkaListener(topics = "numbers")
    public void consume(String message) throws IOException {
        numbers.add(Long.valueOf(message));
        
        this.template.convertAndSend("/topic/numbers", message);
    }
}
