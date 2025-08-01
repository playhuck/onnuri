# 1. 빌드 스테이지 (build-stage)
FROM openjdk:21-jdk-slim as builder

WORKDIR /app

# 소스코드 및 빌드 관련 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 빌드 권한 부여 및 의존성 다운로드
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# 2. 실행 스테이지 (run-stage)
FROM openjdk:21-jdk-slim

WORKDIR /app

# 빌드 스테이지에서 생성된 jar 파일을 최종 이미지로 복사
# build.gradle의 version = '1.0.0'에 따라 파일 이름이 onnuri-1.0.0.jar이 됨
# jar 파일 이름은 '프로젝트명-버전.jar' 형식을 따릅니다.
COPY --from=builder /app/build/libs/onnuri-1.0.0.jar .

EXPOSE 8080

# jar 파일 실행 명령어 수정
CMD ["java", "-jar", "onnuri-1.0.0.jar"]