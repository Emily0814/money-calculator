package com.ajaajas.calc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ajaajas.calc.entity.CalculationHistory;
import com.ajaajas.calc.service.CalcService;
 
@Controller
public class CalcController {

	@Autowired	//주로 클래스의 필드, 생성자, 또는 setter 메서드에 붙여서 Spring 컨테이너가 해당 객체를 자동으로 주입
	private CalcService calcService;
	
	//@GetMapping: HTTP GET 요청을 처리하는 메서드에 사용
	//주로 데이터를 조회할 때 사용
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
	
	// 계산 기록 조회 페이지 (HTML)
	@GetMapping("/history")
	public String historyPage() {
	    return "calculator/history";  
	}
	
	//기본 계산 API
	//@PostMapping: HTTP POST 요청을 처리하는 메서드에 사용
	//주로 데이터를 생성하거나 서버에 전달할 때 사용
	@PostMapping("/api/calculate")
	//@ResponseBody: 반환값을 JSON 또는 XML 형태로 HTTP 응답 본문에 직접 담아서 전송
	//주로 REST API에서 사용됨 (뷰 없이 데이터만 응답할 때)
	@ResponseBody
	public double calculate(@RequestParam("num1") double num1,
							@RequestParam("num2") double num2,
							@RequestParam("operator") String operator) {
		return calcService.basicCalculate(num1, num2, operator);
	}
	
	//이자 계산 API
	@PostMapping("/api/interest")
	@ResponseBody
	public double calculateInterest(@RequestParam("principal") double principal,
									@RequestParam("rate") double rate,
									@RequestParam("years") int years,
									@RequestParam("type") String type) {
		if("simple".equals(type)) {
			return calcService.calculateSimpleInterest(principal, rate, years);
		} else {
			return calcService.calculateCompoundInterest(principal, rate, years);
		}
	}
	
	//대출 상환액 계산 API
	@PostMapping("/api/loan")
	@ResponseBody
	public double calculateLoanPayment(@RequestParam("principal") double principal,
										@RequestParam("rate") double rate,
										@RequestParam("months") int months) {
		return calcService.calculateMonthlyPayment(principal, rate, months);
	}
	
	//환율 조회 API
	@GetMapping("/api/exchange-rate")
	@ResponseBody
	public double getExchangeRate(@RequestParam("from") String from, @RequestParam("to") String to) {
		return calcService.getExchangeRate(from, to);
	}
	
	@PostMapping("/api/exchange")
	@ResponseBody
	public double convertCurrency(@RequestParam("amount") double amount,
								@RequestParam("from") String from,
								@RequestParam("to") String to) {
		return calcService.convertCurrency(amount, from, to);
	}
	
	// 전체 계산 기록 조회 (JSON)
	@GetMapping("/api/history")
	@ResponseBody
	public List<CalculationHistory> getAllHistory() {
	    return calcService.getAllHistory();
	}

	// 계산 타입별 조회 (basic, interest, loan, exchange)
	@GetMapping("/api/history/{type}")
	@ResponseBody  
	public List<CalculationHistory> getHistoryByType(@PathVariable("type") String type) {
	    System.out.println("=== 타입별 기록 조회 요청: " + type + " ===");
	    
	    List<CalculationHistory> result = calcService.getHistoryByType(type);
	    System.out.println("=== 조회 결과 개수: " + result.size() + " ===");
	    
	    return result;
	}

	// 최근 10개 기록
	@GetMapping("/api/history/recent")
	@ResponseBody
	public List<CalculationHistory> getRecentHistory() {
	    return calcService.getRecentHistory();
	}

}
