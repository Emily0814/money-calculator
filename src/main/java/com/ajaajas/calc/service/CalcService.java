package com.ajaajas.calc.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajaajas.calc.entity.CalculationHistory;
import com.ajaajas.calc.repository.CalculationHistoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * CalcService 클래스는 다양한 계산 기능을 제공하는 클래스
 * - 기본 사칙연산
 * - 단리 및 복리 계산
 * - 대출 월 상환금 계산(등액상환 방식)
 * - 실시간 환율 조회 및 환전 계산(ExchangeRate-API)
 * - 모든 계산 내역을 DB에 저장 및 조회
 * 
 * @author ajaajas
 * @version 1.0
 * @since 2025
 */
@Service
public class CalcService {

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * 계산 내역을 저장하는 Repository 객체
	 * Spring의 의존성 주입을 통해 자동으로 주입
	 */
	@Autowired
	private CalculationHistoryRepository historyRepository;
	
	/**
	 * 기본 사칙연산을 수행
	 * 
	 * 지원하는 연산자:
	 * - "+": 덧셈
	 * - "-": 뺄셈
	 * - "*": 곱셈
	 * - "/": 나눗셈(0으로 나누기 방지)
	 * 
	 * @param num1		첫 번째 피연산자
	 * @param num2		두 번째 피연산자
	 * @param operator	연산자(+, -, *, /)
	 * @return 계산 결과
	 * @throws IllegalArgumentException 잘못된 연산자이거나 0으로 나누기를 시도할 경우
	 */
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

	/**
	 * 단리 이자를 계산
	 * 
	 * 공식: 원금 + (원금 * 이율 / 100 * 기간)
	 * 
	 * @param principal	원금(원)
	 * @param rate		연 이율(%)
	 * @param years		투자 기간(년)
	 * @return  단리 적용 후 총 금액(원)
	 */
	public double calculateSimpleInterest(double principal, double rate, int years) {
		double result = principal + (principal * rate / 100 * years);
		
		//DB에 저장
	    saveCalculationHistory("interest", 
	        String.format("{\"principal\":%.2f,\"rate\":%.2f,\"years\":%d,\"type\":\"simple\"}", principal, rate, years),
	        String.valueOf(result));
	    
	    System.out.println("단리 계산 완료 및 DB 저장: " + principal + "원, " + rate + "%, " + years + "년 = " + result + "원");
	    
	    return result;
	}
	
	/**
	 * 복리 이자를 계산
	 * 
	 * 공식: 원금 * (1 + 이율 / 100)^기간
	 * 
	 * @param principal	원금(원)
	 * @param rate		연 이율(%)
	 * @param years		투자 기간(년)
	 * @return  복리 적용 후 총 금액(원)
	 */
	public double calculateCompoundInterest(double principal, double rate, int years) {
		double result = principal * Math.pow(1+rate/100, years);
		
		//DB에 저장
        saveCalculationHistory("interest", 
            String.format("{\"principal\":%.2f,\"rate\":%.2f,\"years\":%d,\"type\":\"compound\"}", principal, rate, years),
            String.valueOf(result));
        
        System.out.println("복리 계산 완료 및 DB 저장: " + principal + "원, " + rate + "%, " + years + "년 = " + result + "원");
		
		return result;
	}
	
	/**
	 * 대출의 월 상환액을 계산(등액상환 방식)
	 * 
	 * 이율이 0%인 경우: 원금을 상환개월수로 나눈 값
	 * 이율이 있는 경우: 원리금균등상환 공식 적용
	 * 
	 * @param principal		대출 원금(원)
	 * @param annualRate	연 이율(%)
	 * @param months		상환 기간(개월)
	 * @return  월 상환액(원, 소수점 둘째자리까지)
	 */
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
	
	/**
	 * 두 통화 간의 환율을 조회
	 * 
	 * ExchangeRate-API(frankfurter.app)를 사용하여 실시간 환율을 가져옴
	 * API 호출 실패 시 미리 정의된 더미 데이터를 반환
	 * 
	 * @param fromCurrency	기준 통화 코드(예: "USD", "KRW")
	 * @param toCurrency	변환 대상 통화 코드(예: "EUR", "JPY")
	 * @return  환율(1 기준통화 = ? 대상통화)
	 * @throws RuntimeException 지원하지 않는 통화 코드인 경우
	 */
	public double getExchangeRate(String fromCurrency, String toCurrency) {
	    try {
	        //ExchangeRate-API 사용 (무료)
	        String url = "https://api.frankfurter.app/latest?from=" + fromCurrency + "&to=" + toCurrency;

	        
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
	/**
	 * 통화를 환전
	 * 
	 * 실시간 환율을 조회하여 금액을 변환
	 * 결과는 소수점 둘째자리까지 반올림
	 * 
	 * @param amount		환전할 금액
	 * @param fromCurrency	기준 통화 코드
	 * @param toCurrency	변환할 통화 코드
	 * @return  환전된 금액(소수점 둘째자리까지)
	 */
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
	/**
	 * 계산 결과를 DB에 저장
	 * 
	 * 모든 계산 유형(basic, interest, loan, exchange)에 대해 
	 * 입력 데이터와 결과를 JSON 형태로 저장
	 * 
	 * @param calcType	계산 유형("basic", "interest", "loan", "exchange")
	 * @param inputData 입력 데이터(JSON 형식 문자열)
	 * @param result	계산 결과(문자열)
	 */
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
	/**
	 * 모든 계산 기록을 조회
	 * 
	 * @return  전체 계산 기록 리스트
	 */
	public List<CalculationHistory> getAllHistory() {
	    return historyRepository.findAll();
	}

	/**
	 * 특정 계산 유형의 기록만 조회
	 * 
	 * @param calcType	조회할 계산 유형("basic", "interest", "loan", "exchange")
	 * @return	해당 유형의 계산 기록 리스트
	 */
	public List<CalculationHistory> getHistoryByType(String calcType) {
	    return historyRepository.findByCalcType(calcType);
	}

	/**
	 * 최근 계산 기록 10개를 조회
	 * 
	 * 생성일시 기준 내림차순으로 정렬되어 반환
	 * 
	 * @return 최근 계산 기록 10개 리스트
	 */
	public List<CalculationHistory> getRecentHistory() {
	    return historyRepository.findTop10ByOrderByCreatedAtDesc();
	}
}
