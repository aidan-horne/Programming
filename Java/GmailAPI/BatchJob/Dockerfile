# Use the official OpenJDK image for Linux
FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Copy your JAR file into the container
COPY GmailSendMain.jar /app/GmailSendMain.jar

# Run the application
CMD ["java", "-jar", "GmailSendMain.jar"]
