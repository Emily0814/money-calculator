package com.ajaajas.calc.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajaajas.calc.entity.CalculationHistory;
import com.ajaajas.calc.repository.CalculationHistoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CalcService {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final String API_KEY = "LpbGexSlwUFpoKJJftExQuhLv6wbYcNc";
	
	//Repository 마법 주입
	@Autowired
	private CalculationHistoryRepository historyRepository;
	
	//기본 사칙연산
	public double basicCalculate(double num1, double num2, String operator) {
		System.out.println("=== basicCalculate 메서드 시작 ===");
	    System.out.println("입력값: " + num1 + " " + operator + " " + num2);
		
		double result;
		
		try {
	        System.out.println("=== switch 문 시작 ===");
	        
	        switch(operator) {
	            case "+": 
	                System.out.println("덧셈 케이스 실행");
	                result = num1 + num2; 
	                break;
	            case "-": 
	                System.out.println("뺄셈 케이스 실행");
	                result = num1 - num2; 
	                break;
	            case "*": 
	                System.out.println("곱셈 케이스 실행");
	                result = num1 * num2; 
	                break;
	            case "/":
	                System.out.println("나눗셈 케이스 실행");
	                if(num2 == 0) {
	                    throw new IllegalArgumentException("0으로 나눌 수 없습니다.");
	                }
	                result = num1 / num2;
	                break;
	            default:
	                System.out.println("잘못된 연산자: '" + operator + "'");
	                throw new IllegalArgumentException("잘못된 연산자입니다.");
	        }
	        
	        System.out.println("=== switch 문 완료, 결과: " + result + " ===");
	        
	    } catch (Exception e) {
	        System.out.println("=== switch 문에서 예외 발생: " + e.getMessage() + " ===");
	        throw e; // 예외를 다시 던져서 에러 확인
	    }
		
		System.out.println("=== DB 저장 시도 ===");
		
		//DB에 저장 시도
	    try {
	        saveCalculationHistory("basic",
	            String.format("{\"num1\":%.2f,\"num2\":%.2f,\"operator\":\"%s\"}", num1, num2, operator),
	            String.valueOf(result));
	        System.out.println("=== DB 저장 메서드 호출 완료 ===");
	    } catch (Exception e) {
	        System.out.println("=== DB 저장 중 예외 발생: " + e.getMessage() + " ===");
	        e.printStackTrace();
	    }
		
		return result;
	}

	//단리 계산
	public double calculateSimpleInterest(double principal, double rate, int years) {
		double result = principal + (principal * rate / 100 * years);
		
		//DB에 저장
	    saveCalculationHistory("interest", 
	        String.format("{\"principal\":%.2f,\"rate\":%.2f,\"years\":%d,\"type\":\"simple\"}", principal, rate, years),
	        String.valueOf(result));
	    
	    System.out.println("단리 계산 완료 및 DB 저장: " + principal + "원, " + rate + "%, " + years + "년 = " + result + "원");
	    
	    return result;
	}
	
	//복리 계산
	public double calculateCompoundInterest(double principal, double rate, int years) {
		double result = principal * Math.pow(1+rate/100, years);
		
		//DB에 저장
        saveCalculationHistory("interest", 
            String.format("{\"principal\":%.2f,\"rate\":%.2f,\"years\":%d,\"type\":\"compound\"}", principal, rate, years),
            String.valueOf(result));
        
        System.out.println("복리 계산 완료 및 DB 저장: " + principal + "원, " + rate + "%, " + years + "년 = " + result + "원");
		
		return result;
	}
	
	//월 상환액 계산(등액상환)
	public double calculateMonthlyPayment(double principal, double annualRate, int months) {
		double result;
		
		if(annualRate == 0) {
			result = principal / months;
		}
		
		double monthlyRate = annualRate / 100 / 12;
		result = principal * (monthlyRate*Math.pow(1+monthlyRate, months))
						/ (Math.pow(1+monthlyRate, months)-1);
		result = Math.round(result * 100.0) / 100.0;		//소수 둘째자리까지
		
		//DB에 저장
        saveCalculationHistory("loan", 
            String.format("{\"principal\":%.2f,\"rate\":%.2f,\"months\":%d}", principal, annualRate, months),
            String.valueOf(result));
        
        System.out.println("대출 계산 완료 및 DB 저장: " + principal + "원, " + annualRate + "%, " + months + "개월 = " + result + "원");
        
        return result;
	}
	
	//최근 영업일 계산
	private String getLatestBusinessDate() {
		
		LocalDate today = LocalDate.now();
		
		//토요일(6)이면 금요일로, 일요일(7)이면 금요일로
		while (today.getDayOfWeek() == DayOfWeek.SATURDAY || today.getDayOfWeek() == DayOfWeek.SUNDAY) {
			today = today.minusDays(1);
		}
		
		return today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	}
	
	//환율 계산 기능 - ExchangeRate-API 활용
	public double getExchangeRate(String fromCurrency, String toCurrency) {
	    try {
	        //ExchangeRate-API 사용 (무료)
	        String url = "https://api.exchangerate-api.com/v6/latest/" + fromCurrency;
	        
	        System.out.println("환율 API 호출: " + url);
	        
	        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	        connection.setRequestMethod("GET");
	        connection.setConnectTimeout(5000);
	        connection.setReadTimeout(5000);
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        StringBuilder response = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            response.append(line);
	        }
	        reader.close();
	        
	        //JSON 파싱
	        JsonNode jsonNode = objectMapper.readTree(response.toString());
	        JsonNode rates = jsonNode.get("rates");
	        
	        if (rates.has(toCurrency)) {
	            double rate = rates.get(toCurrency).asDouble();
	            System.out.println("환율 조회 성공: 1 " + fromCurrency + " = " + rate + " " + toCurrency);
	            return rate;
	        }
	        
	        throw new RuntimeException("지원하지 않는 통화: " + toCurrency);
	        
	    } catch (Exception e) {
	        System.out.println("환율 API 실패, 더미 데이터 사용: " + e.getMessage());
	        
	        //API 실패 시 더미 데이터 사용
	        if ("KRW".equals(fromCurrency) && "USD".equals(toCurrency)) {
	            return 0.00073;
	        } else if ("USD".equals(fromCurrency) && "KRW".equals(toCurrency)) {
	            return 1381.4;
	        } else if ("KRW".equals(fromCurrency) && "EUR".equals(toCurrency)) {
	            return 0.000636;
	        } else if ("EUR".equals(fromCurrency) && "KRW".equals(toCurrency)) {
	            return 1571.14;
	        } else if ("KRW".equals(fromCurrency) && "JPY".equals(toCurrency)) {
	            return 0.104;
	        } else if ("JPY".equals(fromCurrency) && "KRW".equals(toCurrency)) {
	            return 9.59;
	        }
	        
	        return 1.0;	//기본값

	    }
	}
	
	//환전 계산
	public double convertCurrency(double amount, String fromCurrency, String toCurrency) {
		double rate = getExchangeRate(fromCurrency, toCurrency);
		double result = Math.round(amount * rate * 100.0) / 100.0;
		
		//DB에 저장!
		saveCalculationHistory("exchange", 
	            String.format("{\"amount\":%.2f,\"from\":\"%s\",\"to\":\"%s\",\"rate\":%.6f}", amount, fromCurrency, toCurrency, rate),
	            String.valueOf(result));
	        
		System.out.println("환전 계산 완료 및 DB 저장: " + amount + " " + fromCurrency + " = " + result + " " + toCurrency);
	        
		return result;
	}
	
	//DB 저장 공통 메서드
	private void saveCalculationHistory(String calcType, String inputData, String result) {
	    try {
	        CalculationHistory history = new CalculationHistory(calcType, inputData, result);
	        historyRepository.save(history);
	        System.out.println("✅ DB 저장 성공: " + calcType);
	    } catch (Exception e) {
	        System.err.println("❌ DB 저장 실패: " + e.getMessage());
	    }
	}
	
	//계산 기록 조회 메서드들
	public List<CalculationHistory> getAllHistory() {
	    return historyRepository.findAll();
	}

	public List<CalculationHistory> getHistoryByType(String calcType) {
	    return historyRepository.findByCalcType(calcType);
	}

	public List<CalculationHistory> getRecentHistory() {
	    return historyRepository.findTop10ByOrderByCreatedAtDesc();
	}
}
