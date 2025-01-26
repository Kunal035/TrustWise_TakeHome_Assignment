# Step 1: Build the Dropwizard Java app using Maven
FROM maven:3.8.4-openjdk-11 AS build-java
WORKDIR /app

# Copy the Maven POM file and source code
COPY pom.xml . 
COPY src ./src

# Build the Dropwizard Java backend (which includes app.js in the JAR)
RUN mvn clean package -DskipTests

# Add this line to install curl
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Step 2: Set up the final Java environment
FROM openjdk:22-oracle AS final

# Step 3: Combine the Java environments
WORKDIR /app

# Copy the built Dropwizard JAR file from the build stage
COPY --from=build-java /app/target/flipfit_d-1.0-SNAPSHOT.jar ./flipfit_d.jar

# Copy the Dropwizard configuration file
COPY config.yml ./config.yml

# Expose ports for Dropwizard (8080)
EXPOSE 8080

# Start the Dropwizard service
CMD java -jar flipfit_d.jar server config.yml
