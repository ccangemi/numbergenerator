package com.rocketscience.numbergenerator.viewer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.rocketscience.numbergenerator.viewer.services.NumberGeneratorViewerService;

@Controller
public class NumberGeneratorViewerController {
	
	@Autowired
	private NumberGeneratorViewerService numberGeneratorViewerService;
	
	@GetMapping("/")
	public String view(Model model) {
		return "index";
	}

	@GetMapping("/numbers")
	public String moreNumbers(Model model) {
		model.addAttribute("numbers", numberGeneratorViewerService.getNumbers());
		return "fragments :: #data";
	}
}
