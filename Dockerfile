# Alpine Linux with OpenJDK JRE
FROM openjdk:11
mvn clean install
# copy WAR into image
COPY modnyi-0.0.1-SNAPSHOT.jar /app.jar
# run application with this command line
CMD ["java", "-jar", "/app.jar"]