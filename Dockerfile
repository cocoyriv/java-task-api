# Step 1: Build the Java application using Maven
FROM maven:3.9-eclipse-temurin-19 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Step 2: Run the application using a lightweight Java 19 runtime
FROM eclipse-temurin:19-jre-jammy
WORKDIR /app
COPY --from=build /app/target/task-tracker-1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
