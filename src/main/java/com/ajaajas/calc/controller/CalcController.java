package com.ajaajas.calc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ajaajas.calc.service.CalcService;

@Controller
public class CalcController {

	@Autowired	//주로 클래스의 필드, 생성자, 또는 setter 메서드에 붙여서 Spring 컨테이너가 해당 객체를 자동으로 주입
	private CalcService calcService;
	
	//메인 페이지
	@GetMapping("/")
	public String home() {
		return "index";
	}
	
	//기본 계산기 페이지
	@GetMapping("/basic")
	public String basicCalculator() {
		return "calculator/basic";
	}
	
	//이자 계산기 페이지
	@GetMapping("/interest")
	public String interestCalculator() {
		return "calculator/interest";
	}
	
	//대출 계산기 페이지
	@GetMapping("/loan")
	public String loanCalculator() {
		return "calculator/loan";
	}
	
	//환율 계산기 페이지
	@GetMapping("/exchange")
	public String exchangeCalculator() {
		return "calculator/exchange";
	}
	
	//기본 계산 API
	public double calculate(@RequestParam double num1,
							@RequestParam double num2,
							@RequestParam String operator) {
		return calcService.basicCalculate(num1, num2, operator);
	}
	
	//이자 계산 API
	public double calculateInterest(@RequestParam double principal,
									@RequestParam double rate,
									@RequestParam int years,
									@RequestParam String type) {
		if("simple".equals(type)) {
			return calcService.calculateSimpleInterest(principal, rate, years);
		} else {
			return calcService.calculateCompoundInterest(principal, rate, years);
		}
	}
	
	//대출 상환액 계산 API
	public double calculateLoanPayment(@RequestParam double principal,
										@RequestParam double rate,
										@RequestParam int months) {
		return calcService.calculateMonthlyPayment(principal, rate, months);
	}
	
}
