# OpenJDK 17 이미지 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /usr/app

# 애플리케이션 JAR 파일 복사
COPY build/libs/*.jar myapp.jar

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=prod
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql-container:3306/baro_intern
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=12345678

# 8080 포트 오픈
EXPOSE 8080

# 컨테이너 실행 시 실행할 명령어
ENTRYPOINT ["java", "-jar", "myapp.jar"]