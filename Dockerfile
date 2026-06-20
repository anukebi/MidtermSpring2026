FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn

# Convert CRLF to LF for mvnw just in case it was checked out on Windows
RUN sed -i 's/\r$//' mvnw
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/uno-0.0.1-SNAPSHOT.jar uno.jar

ENTRYPOINT ["java", "-jar", "uno.jar"]
