# Step 1: Use a base image with JDK 17
FROM eclipse-temurin:17-jdk-alpine AS build

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the Gradle build files and the application source code to the container
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY src ./src

# Step 4: Set execute permissions on Gradle wrapper
RUN chmod +x gradlew

# Step 5: Build the Spring Boot application using Gradle
RUN ./gradlew build -x test --no-daemon

# Step 6: Create a new image with JRE 17 to run the application
FROM eclipse-temurin:17-jre-alpine

# Step 7: Set the working directory for the runtime
WORKDIR /app

# Step 8: Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Step 9: Expose the application port
EXPOSE 8080

# Step 10: Define the entry point for the container to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]