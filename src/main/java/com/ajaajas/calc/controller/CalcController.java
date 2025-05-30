package com.ajaajas.calc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalcController {

	//메인 페이지
	@GetMapping("/")
	public String home() {
		return "index";
	}
	
}
