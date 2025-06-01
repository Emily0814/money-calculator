package com.ajaajas.calc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "calculation_history")
public class CalculationHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calc_seq")
    @SequenceGenerator(name = "calc_seq", sequenceName = "CALC_SEQ", allocationSize = 1)
    private Long id;
    
    @Column(name = "calc_type", nullable = false, length = 20)
    private String calcType;  // "basic", "interest", "loan", "exchange"
    
    @Column(name = "input_data", length = 1000)
    private String inputData; // JSON 형태로 입력값 저장
    
    @Column(name = "result", nullable = false)
    private String result;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // 기본 생성자
    public CalculationHistory() {
        this.createdAt = LocalDateTime.now();
    }
    
    // 생성자
    public CalculationHistory(String calcType, String inputData, String result) {
        this.calcType = calcType;
        this.inputData = inputData;
        this.result = result;
        this.createdAt = LocalDateTime.now();
    }
    
    //Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCalcType() { return calcType; }
    public void setCalcType(String calcType) { this.calcType = calcType; }
    
    public String getInputData() { return inputData; }
    public void setInputData(String inputData) { this.inputData = inputData; }
    
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
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