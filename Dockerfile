# ── 1단계: 빌드 전용 (JDK + 소스 코드로 jar를 만든 뒤 버려짐) ──
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Gradle 설정 파일만 먼저 복사해서 의존성을 받아 둔다.
# Docker는 줄마다 결과를 캐시하므로, 소스만 바뀌었을 때는 이 단계를 다시 하지 않아 빌드가 빨라진다.
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon > /dev/null

# 소스 복사 후 실행용 jar 생성 (테스트는 MySQL이 필요하므로 이미지 빌드에서는 제외, CI에서 따로 돌린다)
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# ── 2단계: 실행 전용 (JRE + jar 하나만 담긴 최종 이미지) ──
FROM eclipse-temurin:21-jre
WORKDIR /app

# 1단계 결과물에서 jar만 가져온다. 소스 코드와 빌드 도구는 최종 이미지에 들어가지 않는다.
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
