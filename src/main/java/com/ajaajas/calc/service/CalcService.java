package com.ajaajas.calc.service;

import org.springframework.stereotype.Service;

@Service
public class CalcService {

	//기본 사칙연산
	public double basicCalculate(double num1, double num2, String operator) {
		switch(operator) {
			case "+":
				return num1 + num2;
			case "-":
				return num1 - num2;
			case "*":
				return num1 * num2;
			case "/":
				if(num2 == 0) {
					throw new IllegalArgumentException("0으로 나눌 수 없습니다.");
				}
				return num1 / num2;
			default:
				throw new IllegalArgumentException("잘못된 연산자입니다.");
			
		}
	}
	
	//단리 계산
	public double calculateSimpleInterest(double principal, double rate, int years) {
		return principal + (principal * rate / 100 * years);
	}
	
	//복리 계산
	public double calculateCompoundInterest(double principal, double rate, int years) {
		return principal + Math.pow(1+rate/100, years);
	}
	
	//월 상환액 계산(등액상환)
	public double calculateMonthlyPayment(double principal, double annualRate, int months) {
		if(annualRate == 0) {
			return principal / months;
		}
		
		double monthlyRate = annualRate / 100 / 12;
		double payment = principal * (monthlyRate*Math.pow(1+monthlyRate, months))
						/ (Math.pow(1+monthlyRate, months)-1);
		return Math.round(payment * 100.0) / 100.0;		//소수 둘째자리까지
	}
	
}
