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
 
/**
 * 계산기 기능을 제공하는 Spring MVC 컨트롤러
 * 
 * 사용자의 요청을 처리하고 결과 또는 페이지를 반환
 * 
 * @author ajaajas
 * @version 1.0
 * @since 2025
 */
@Controller
public class CalcController {
	
	/*
	[사용 어노테이션 정리]
	- @Controller: 이 클래스를 Spring 웹 컨트롤러로 등록
	- @GetMapping: HTTP GET 요청을 처리할 메서드에 사용	> 주로 데이터를 조회할 때 사용
	- @PostMapping: HTTP POST 요청을 처리할 메서드에 사용 > 주로 데이터를 생성하거나 서버에 전달할 때 사용
	- @ResponseBody: 반환값을 JSON 또는 XML 형태로 HTTP 응답 본문에 직접 전달 > 주로 REST API에서 사용됨 (뷰 없이 데이터만 응답할 때)
	- @Autowired: 의존성을 자동 주입
	*/

	/**
	 * CalcService를 의존성 주입 받아 사용
	 */
	@Autowired	//주로 클래스의 필드, 생성자, 또는 setter 메서드에 붙여서 Spring 컨테이너가 해당 객체를 자동으로 주입
	private CalcService calcService;
	
	// ===== [페이지 라우팅 처리 메서드] ======
	
	/**
	 * 메인 페이지 요청 처리
	 * 
	 * @return "index" 뷰 이름(index.html 렌더링)
	 */
	@GetMapping("/")
	public String home() {
		return "index";
	}
	
	/**
	 * 기본 계산기 페이지 요청 처리
	 * 
	 * @return "calculator/basic" 뷰 이름(calculator/basic.html 렌더링)
	 */
	@GetMapping("/basic")
	public String basicCalculator() {
		return "calculator/basic";
	}
	
	/**
	 * 이자 계산기 페이지 요청 처리
	 * 
	 * @return "calculator/interest" 뷰 이름(calculator/interest.html 렌더링)
	 */
	@GetMapping("/interest")
	public String interestCalculator() {
		return "calculator/interest";
	}
	
	/**
	 * 대출 계산기 페이지 요청 처리
	 * 
	 * @return "calculator/loan" 뷰 이름(calculator/loan.html 렌더링)
	 */
	@GetMapping("/loan")
	public String loanCalculator() {
		return "calculator/loan";
	}
	
	/**
	 * 환율 계산기 페이지 요청 처리
	 *
	 * @return "calculator/exchange" 뷰 이름(calculator/exchange.html 렌더링)
	 */
	@GetMapping("/exchange")
	public String exchangeCalculator() {
		return "calculator/exchange";
	}
	
	/**
	 * 계산 기록 조회 페이지 요청 처리
	 * 
	 * @return "calculator/history" 뷰 이름(calculator/history.html 렌더링)
	 */
	@GetMapping("/history")
	public String historyPage() {
	    return "calculator/history";  
	}
	
	// ===== [API 처리 메서드] =====
	
	/**
	 * 기본 사칙연산 계산 API
	 * 
	 * @param num1 첫 번째 숫자(요청 파라미터 "num1")
	 * @param num2 두 번째 숫자(요청 파라미터 "num2")
	 * @param operator 연산자 기호(요청 파라미터 "operator": +,-,*,/ 등)
	 * @return 계산 결과(double)를 JSON으로 반환
	 */
	@PostMapping("/api/calculate")
	@ResponseBody
	public double calculate(@RequestParam("num1") double num1,
							@RequestParam("num2") double num2,
							@RequestParam("operator") String operator) {
		return calcService.basicCalculate(num1, num2, operator);
	}
	
	/**
	 * 이자 계산 API(단리 또는 복리)
	 * 
	 * @param principal 원금
	 * @param rate 이자율(연 이율, % 단위)
	 * @param years 투자 기간(연 단위)
	 * @param type 이자 유형("simple" 또는 "compound")
	 * @return 계산된 이자 금액(JSON으로 반환)
	 */
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
	
	/**
	 * 대출 상환액 계산 API
	 * 
	 * @param principal 대출 원금
	 * @param rate 연이율(%)
	 * @param months 상환 기간(개월 단위)
	 * @return 월별 상환 금액(JSON으로 반환)
	 */
	@PostMapping("/api/loan")
	@ResponseBody
	public double calculateLoanPayment(@RequestParam("principal") double principal,
										@RequestParam("rate") double rate,
										@RequestParam("months") int months) {
		return calcService.calculateMonthlyPayment(principal, rate, months);
	}
	
	/**
	 * 환율 조회 API
	 * 
	 * @param from 기준 통화 코드(예: "USD","KRW")
	 * @param to 대상 통화 코드(예: "EUR","JPY")
	 * @return 환율(from -> to)(JSON으로 반환)
	 */
	@GetMapping("/api/exchange-rate")
	@ResponseBody
	public double getExchangeRate(@RequestParam("from") String from, @RequestParam("to") String to) {
		return calcService.getExchangeRate(from, to);
	}
	
	/**
	 * 통화 환전 API
	 * 
	 * @param amount 환전할 금액
	 * @param from 기준 통화 코드
	 * @param to 대상 통화 코드
	 * @return 환전된 금액(JSON으로 반환)
	 */
	@PostMapping("/api/exchange")
	@ResponseBody
	public double convertCurrency(@RequestParam("amount") double amount,
								@RequestParam("from") String from,
								@RequestParam("to") String to) {
		return calcService.convertCurrency(amount, from, to);
	}
	
	// ===== [계산 기록 관련 메서드] =====

	/**
	 * 전체 계산 기록을 JSON 형태로 조회
	 * 
	 * @return 전체 계산 기록 리스트(JSON으로 반환)
	 */
	@GetMapping("/api/history")
	@ResponseBody
	public List<CalculationHistory> getAllHistory() {
	    return calcService.getAllHistory();
	}

	/**
	 * 계산 타입별 조회(basic, interest, loan, exchange)
	 * 
	 * @param type 계산 유형("basic", "interest", "loan", "exchange")
	 * @return 해당 유형의 계산 기록 리스트(JSON으로 반환)
	 */
	@GetMapping("/api/history/{type}")
	@ResponseBody  
	public List<CalculationHistory> getHistoryByType(@PathVariable("type") String type) {
	    System.out.println("=== 타입별 기록 조회 요청: " + type + " ===");
	    
	    List<CalculationHistory> result = calcService.getHistoryByType(type);
	    System.out.println("=== 조회 결과 개수: " + result.size() + " ===");
	    
	    return result;
	}

	/**
	 * 최근 10개의 계산 기록을 조회
	 * 
	 * @return 최근 10개의 계산 기록 리스트(JSON으로 반환)
	 */
	@GetMapping("/api/history/recent")
	@ResponseBody
	public List<CalculationHistory> getRecentHistory() {
	    return calcService.getRecentHistory();
	}

}
