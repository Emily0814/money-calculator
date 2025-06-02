# 💰 금융 계산기 (Financial Calculator)

> Spring Boot + Oracle DB 기반의 개인 금융 관리 도구  
> 금융 IT 개발자 포트폴리오 프로젝트

## 🎯 프로젝트 개요

- **목적**: 금융 IT 개발자로 취업하기 위한 포트폴리오 제작
- **개발 기간**: 1주일
- **주요 기능**: 4가지 계산기 + 계산 기록 관리 및 통계 대시보드

## 🛠️ 기술 스택

| 구분 | 기술/도구 |
|------|-----------|
| **Frontend** | Thymeleaf + Bootstrap 5 + JavaScript |
| **Backend** | Spring Boot 3.5.0 + Gradle |
| **Database** | Oracle Database |
| **External API** | Frankfurter API (실시간 환율) |

## ✨ 주요 기능

| 기능명 | 설명 |
|--------|------|
| 🧮 **기본 계산기** | 사칙연산 (덧셈, 뺄셈, 곱셈, 나눗셈) 지원 |
| 📈 **이자 계산기** | 복리 이자 계산 기능 |
| 🏠 **대출 계산기** | 월별 상환액 계산 |
| 💱 **환율 계산기** | Frankfurter API를 활용한 실시간 환율 조회 |
| 📊 **계산 기록 관리** | 모든 계산 결과 저장 및 조회 가능 |
| 📈 **통계 대시보드** | 기록된 계산 결과를 기반으로 통계 시각화 |

## 🚀 설치 및 실행

<details><summary>🛠️ 설치 & 실행 가이드</summary>
    
### 사전 요구사항
- Java 21
- Oracle Database
- Gradle

### 실행 방법
```bash
# 1. 저장소 클론
git clone https://github.com/Emily0814/money-calculator.git
cd money-calculator

# 2. 로컬 DB 설정 파일 생성
# src/main/resources/application-local.yml 파일을 생성하고 아래 내용 추가

# 3. 애플리케이션 실행
./gradlew bootRun

# 4. 브라우저에서 접속
http://localhost:8080
```
```yaml
# application-local.yml
spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521/XEPDB1    # host:port/serviceName을 본인 환경에 맞게 수정
    username: calc                                  # Oracle DB 사용자명
    password: calc1234                              # Oracle DB 비밀번호
    driver-class-name: oracle.jdbc.OracleDriver
```

</details>

## 📁 프로젝트 구조
<details><summary>📘구조 보기</summary>
    
```java
src/main/java/com/ajaajas/calc/
├── CalcApplication.java
├── controller/
│   └── CalcController.java
├── service/
│   └── CalcService.java
├── entity/
│   └── CalculationHistory.java
└── repository/
    └── CalculationHistoryRepository.java

src/main/resources/
├── templates/
│   ├── fragments/
│   ├── calculator/
│   └── index.html
└── static/
    ├── css/
    └── js/
```
</details>

## 💻 핵심 코드

### 1. 복리 계산 로직 : Math.pow() 활용한 금융 공식 구현
<details><summary>📋 코드 보기</summary>
    
```java
public double calculateCompoundInterest(double principal, double rate, int years) {
    double result = principal * Math.pow(1 + rate/100, years);
    
    saveCalculationHistory("interest", 
        String.format("{\"principal\":%.2f,\"rate\":%.2f,\"years\":%d,\"type\":\"compound\"}", 
                     principal, rate, years),
        String.valueOf(result));
    
    return result;
}
```
</details>

### 2. 외부 API 연동 및 예외 처리 : Frankfurter API + 예외처리 + 폴백 로직
<details><summary>📋 코드 보기</summary>
    
```java
public double getExchangeRate(String fromCurrency, String toCurrency) {
    try {
        String url = "https://api.frankfurter.app/latest?from=" + fromCurrency + "&to=" + toCurrency;
        
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
        
        JsonNode jsonNode = objectMapper.readTree(response.toString());
        JsonNode rates = jsonNode.get("rates");
        
        if (rates.has(toCurrency)) {
            return rates.get(toCurrency).asDouble();
        }
        
        throw new RuntimeException("지원하지 않는 통화: " + toCurrency);
        
    } catch (Exception e) {
        // API 실패 시 더미 데이터 사용
        if ("KRW".equals(fromCurrency) && "USD".equals(toCurrency)) {
            return 0.00073;
        } else if ("USD".equals(fromCurrency) && "KRW".equals(toCurrency)) {
            return 1381.4;
        }
        return 1.0; // 기본값
    }
}
```

</details>

### 3. Entity 및 Repository 설계 : JPA Entity + Spring Data Repository 패턴

<details><summary>📋 코드 보기</summary>
    
```java
@Entity
@Table(name = "calculation_history")
public class CalculationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calc_seq")
    @SequenceGenerator(name = "calc_seq", sequenceName = "CALC_SEQ", allocationSize = 1)
    private Long id;
    
    @Column(name = "calc_type", nullable = false, length = 20)
    private String calcType;
    
    @Column(name = "input_data", length = 1000)
    private String inputData; // JSON 형태
    
    @Column(name = "result", nullable = false)
    private String result;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

@Repository
public interface CalculationHistoryRepository extends JpaRepository<CalculationHistory, Long> {
    List<CalculationHistory> findByCalcTypeOrderByCreatedAtDesc(String calcType);
    List<CalculationHistory> findTop10ByOrderByCreatedAtDesc();
}
```

</details>

## 🔄 최근 업데이트

✅ **완료된 기능**
- [x] 클라우드 배포 - Render (2025.06.10)

🚧 **개발 예정 기능**
- [ ] Spring Security + 로그인/회원가입
- [ ] 사용자별 계산 기록 분리  
- [ ] OAuth2 소셜 로그인 (Google)
- [ ] 계산 결과 차트/그래프 시각화
- [ ] PDF 리포트 생성

## 🔗 관련 링크

- 📝[프로젝트 개발 후기 (velog)](https://velog.io/@ajaajas/금융IT-개발자되기-프로젝트-Spring-Boot-Oracle-DB-금융-계산기-프로젝트)
- 🌐[배포 사이트 보기](https://money-calculator-hdld.onrender.com)

## 📄 라이선스

MIT License

---

<div align="center">
  <strong>금융 IT 개발자가 되기 위한 여정</strong> 🚀
</div>
