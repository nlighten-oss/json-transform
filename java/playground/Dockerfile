FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
COPY build/libs/*.jar app.jar
EXPOSE 10000
ENTRYPOINT ["java","-jar","/app.jar"]