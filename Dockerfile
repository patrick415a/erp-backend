FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
# 실행권한
RUN chmod +x ./gradlew
# 복사한 코드를 바탕으로 gradle build 실행해서 jar로 실행
RUN ./gradlew clean build -x test
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
# SQLite DB 파일 복사 (추가!)
COPY --from=build /app/erp_database.db /app/erp_database.db
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]