# Use Official Scala image
FROM openjdk:11

# Set working directory
WORKDIR /app

# Copy build files
COPY target/scala-2.13/bank-app.jar /app/bank-app.jar

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "/app/bank-app.jar"]