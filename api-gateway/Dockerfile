FROM maven:3.10-eclipse-temurin-25-alpine AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -B

FROM eclipse-temurin:25-jre-alpine AS runner

WORKDIR /app

COPY --from=builder /app/target/api-gateway-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 4004

#fix for java4shell
ENV JDK_JAVA_OPTIONS="-XX:-UseBASE64Intrinsics"

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]