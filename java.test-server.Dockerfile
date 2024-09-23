FROM gradle:8.4-jdk17 as build
ARG PORT=10000
WORKDIR /
COPY java/json-transform java/json-transform
COPY java/playground/src java/playground/src
COPY java/playground/build.gradle java/playground
COPY settings.gradle .

WORKDIR /java/playground
RUN gradle task bootJar

FROM eclipse-temurin:17-jdk-jammy as app
COPY --from=build /java/playground/build/libs/playground-1.0.0.jar .
EXPOSE $PORT
CMD ["java", "-jar", "./playground-1.0.0.jar"]
