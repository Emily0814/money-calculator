package com.ajaajas.calc.repository;

import com.ajaajas.calc.entity.CalculationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CalculationHistoryRepository extends JpaRepository<CalculationHistory, Long> {
    
    //메서드 이름만으로 쿼리 자동 생성
    List<CalculationHistory> findByCalcType(String calcType);
    
    //복합 조건도 메서드 이름으로
    List<CalculationHistory> findByCalcTypeAndCreatedAtAfter(String calcType, LocalDateTime date);
    
    //정렬도 메서드 이름으로
    List<CalculationHistory> findByCalcTypeOrderByCreatedAtDesc(String calcType);
    
    //개수 제한도
    List<CalculationHistory> findTop10ByOrderByCreatedAtDesc();
    
    //직접 쿼리도 가능
    @Query("SELECT ch FROM CalculationHistory ch WHERE ch.calcType = :type AND ch.createdAt >= :date")
    List<CalculationHistory> findRecentCalculations(@Param("type") String type, @Param("date") LocalDateTime date);
    
    //통계 쿼리도
    @Query("SELECT ch.calcType, COUNT(ch) FROM CalculationHistory ch GROUP BY ch.calcType")
    List<Object[]> getCalculationStats();
}