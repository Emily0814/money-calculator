FROM openjdk:21-jdk-slim

WORKDIR /app

# Gradle Wrapper와 빌드 파일들 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 실행 권한 부여
RUN chmod +x gradlew

# 의존성만 먼저 다운로드 (캐싱 최적화)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src src

# 애플리케이션 빌드
RUN ./gradlew build -x test --no-daemon

# 포트 노출
EXPOSE 8080

# JAR 파일명을 정확히 확인하세요!
CMD ["java", "-jar", "build/libs/calc-0.0.1-SNAPSHOT.jar"]