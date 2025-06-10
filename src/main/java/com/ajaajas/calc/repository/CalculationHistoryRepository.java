package com.ajaajas.calc.repository;

import com.ajaajas.calc.entity.CalculationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * {@link CalculationHistory} 엔티티에 접근하기 위한 리포지토리 인터페이스
 * Spring Data JPA를 활용하여 계산 기록(CalculationHistory)에 대한 다양한 조회 기능을 제공
 * 
 * @author ajaajas
 * @version 1.0
 * @since 2025
 */
@Repository
public interface CalculationHistoryRepository extends JpaRepository<CalculationHistory, Long> {
    
    //메서드 이름만으로 쿼리 자동 생성
    /**
     * 특정 계산 타입(calcType)에 해당하는 모든 계산 기록을 조회
     * 
     * @param calcType 계산 타입(예: "basic", "interest", "loan", "exchange")
     * @return 해당 타입의 계산 기록 리스트
     */
    List<CalculationHistory> findByCalcType(String calcType);
    
    //복합 조건도 메서드 이름으로
    /**
     * 특정 계산 타입이며, 지정한 시각 이후에 생성된 계산 기록을 조회
     * 
     * @param calcType 계산 타입
     * @param date 기준 시간
     * @return	조건을 만족하는 계산 기록 리스트
     */ 
    List<CalculationHistory> findByCalcTypeAndCreatedAtAfter(String calcType, LocalDateTime date);
    
    //정렬도 메서드 이름으로
    /**
     * 특정 계산 타입에 해당하는 계산 기록을 생성일 기준으로 내림차순 정렬하여 조회
     * 
     * @param calcType 계산 타입
     * @return 내림차순 정렬된 계산 기록 리스트
     */
    List<CalculationHistory> findByCalcTypeOrderByCreatedAtDesc(String calcType);
    
    //개수 제한도
    /**
     * 생성일 기준으로 가장 최근의 계산 기록 10건을 조회
     * 
     * @return 최근 계산 기록 10개 리스트
     */
    List<CalculationHistory> findTop10ByOrderByCreatedAtDesc();
    
    //직접 쿼리도 가능
    /**
     * 직접 JPQL을 사용하여 특정 계산 타입이면서 지정한 시간 이후에 생성된 계산 기록을 조회
     * 
     * @param calcType 계산 타입
     * @param date 기준 시간
     * @return 조건을 만족하는 계산 기록 리스트
     */
    @Query("SELECT ch FROM CalculationHistory ch WHERE ch.calcType = :calcType AND ch.createdAt >= :date")
    List<CalculationHistory> findRecentCalculations(@Param("calcType") String calcType, @Param("date") LocalDateTime date);
    
    //통계 쿼리도 가능
    /**
     * 계산 타입별로 그룹화하여 각 타입별 계산 기록 수를 반환
     * 
     * 반환 형식: Object[] 배열의 리스트
     * - 배열의 첫 번째 요소: 계산 타입(String)
     * - 배열의 두 번째 요소: 해당 타입의 계산 기록 수(Long)
     * 
     * @return 계산 타입별 기록 수 통계
     */
    @Query("SELECT ch.calcType, COUNT(ch) FROM CalculationHistory ch GROUP BY ch.calcType")
    List<Object[]> getCalculationStats();
}