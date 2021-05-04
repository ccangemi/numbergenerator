package com.rocketscience.numbergenerator.platform.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rocketscience.numbergenerator.lib.NumberGenerator;

@RestController
public class NumberGeneratorController {
	
	@Autowired
	private NumberGenerator numberGenerator;
	
	/**
	 * Returns an integer random number: value between 1 and maxValue (defaults to 100).
	 * @param maxValue max value the generated number can be
	 * @return random number
	 */
	@GetMapping("/random")
	public long random(@RequestParam(value="maxValue", required = false) Long maxValue) {
		return numberGenerator.random(maxValue);
	}
}
