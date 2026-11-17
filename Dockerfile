FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
# 복사한 코드를 바탕으로 gradle build 실행해서 jar로 실행
RUN ./gradlew clean build -x test
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]