FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S app && adduser -S app -G app \
    && mkdir -p /app/NFEs \
    && chown -R app:app /app

COPY --from=build /workspace/target/sutil-nfe-0.0.1-SNAPSHOT.jar /app/app.jar

USER app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
