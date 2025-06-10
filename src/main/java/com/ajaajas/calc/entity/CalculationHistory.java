package com.ajaajas.calc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 계산 기록을 저장하는 JPA 엔티티 클래스
 * 
 * 계산 유형, 입력 데이터, 결과, 생성 시각 등을 포함하며, 계산 이력을 DB에 저장하기 위한 용도로 사용
 * 
 * @author ajaajas
 * @version 1.0
 * @since 2025 
 */
@Entity
@Table(name = "calculation_history")
public class CalculationHistory {
    
    /**
     * 기본 키(자동 증가 시퀀스 사용)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calc_seq")
    @SequenceGenerator(name = "calc_seq", sequenceName = "CALC_SEQ", allocationSize = 1)
    private Long id;
    
    /**
     * 계산 유형(예: "basic", "interest", "loan", "exchange")
     */
    @Column(name = "calc_type", nullable = false, length = 20)
    private String calcType;
    
    /**
     * 계산에 사용된 입력 데이터(JSON 형식)
     */
    @Column(name = "input_data", length = 1000)
    private String inputData; // JSON 형태로 입력값 저장
    
    /**
     * 계산 결과
     */
    @Column(name = "result", nullable = false)
    private String result;
    
    /**
     * 계산 기록 생성 시간
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * 기본 생성자
     * 생성 시점의 시간으로 createdAt 필드 초기화
     */
    public CalculationHistory() {
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * 모든 필드를 초기화하는 생성자
     * @param calcType 계산 유형
     * @param inputData 입력 데이터(JSON)
     * @param result 계산 결과
     */
    public CalculationHistory(String calcType, String inputData, String result) {
        this.calcType = calcType;
        this.inputData = inputData;
        this.result = result;
        this.createdAt = LocalDateTime.now();
    }
    
    //Getter & Setter
    
    /**
     * @return 고유 ID
     */
    public Long getId() { return id; }
    /**
     * @param id 고유 ID 설정
     */
    public void setId(Long id) { this.id = id; }
    
    /**
     * @return 계산 유형
     */
    public String getCalcType() { return calcType; }
    /**
     * @param calcType 계산 유형 설정
     */
    public void setCalcType(String calcType) { this.calcType = calcType; }
    
    /**
     * @return 입력 데이터(JSON)
     */
    public String getInputData() { return inputData; }
    /**
     * @param inputData 입력 데이터 설정(JSON)
     */
    public void setInputData(String inputData) { this.inputData = inputData; }
    
    /**
     * @return 계산 결과
     */
    public String getResult() { return result; }
    /**
     * @param result 계산 결과 설정
     */
    public void setResult(String result) { this.result = result; }
    
    /**
     * @return 계산 기록 생성 시간
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * @param createdAt 계산 기록 생성 시각 설정
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    /**
     * 객체 정보를 문자열로 반환
     * @return 문자열 표현
     */
    @Override
    public String toString() {
        return "CalculationHistory{" +
                "id=" + id +
                ", calcType='" + calcType + '\'' +
                ", inputData='" + inputData + '\'' +
                ", result='" + result + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}