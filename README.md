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

### 사전 요구사항
- Java 21
- Oracle Database
- Gradle

### 실행 방법
```bash
# 1. 저장소 클론
git clone https://github.com/Emily0814/money-calculator.git
cd money-calculator

# 2. Oracle DB 설정
# application.yml에서 DB 정보 수정

# 3. 애플리케이션 실행
./gradlew bootRun

# 4. 브라우저에서 접속
http://localhost:8080
```

## 📁 프로젝트 구조

```
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

## 💻 핵심 코드

### Entity 설계
```java
@Entity
@Table(name = "calculation_history")
public class CalculationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column(name = "calc_type")
    private String calcType; // "basic", "interest", "loan", "exchange"
    
    @Column(name = "input_data", length = 1000)
    private String inputData; // JSON 형태
    
    // ...
}
```

### REST API 설계
```java
@Controller
public class CalcController {
    
    @PostMapping("/api/calculate")
    @ResponseBody
    public double calculate(@RequestParam("num1") double num1,
                           @RequestParam("num2") double num2,
                           @RequestParam("operator") String operator) {
        return calcService.basicCalculate(num1, num2, operator);
    }
    
    @GetMapping("/api/history/{type}")
    @ResponseBody
    public List<CalculationHistory> getHistoryByType(@PathVariable("type") String type) {
        return calcService.getHistoryByType(type);
    }
}
```

## 🔄 최근 업데이트

✅ **완료된 기능**


🚧 **개발 예정 기능**
- [ ] Spring Security + 로그인/회원가입 (2025.06.10 추가)
- [ ] 사용자별 계산 기록 분리  
- [ ] OAuth2 소셜 로그인 (Google)
- [ ] 계산 결과 차트/그래프 시각화
- [ ] PDF 리포트 생성
- [ ] 클라우드 배포

## 🔗 관련 링크

- [📝 프로젝트 개발 후기 (velog)](https://velog.io/@ajaajas/금융IT-개발자되기-프로젝트-Spring-Boot-Oracle-DB-금융-계산기-프로젝트)

## 📄 라이선스

MIT License

---

<div align="center">
  <strong>금융 IT 개발자가 되기 위한 여정</strong> 🚀
</div>
