FROM maven:latest AS build
WORKDIR /usr/src/motive-back-end
COPY pom.xml .
RUN mvn -B -f pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:resolve
COPY . .
RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml package -D skipTests

FROM openjdk:11
WORKDIR /app
COPY --from=build /usr/src/motive-back-end/target/motive-back-end-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar", "/app/motive-back-end-0.0.2-SNAPSHOT.jar"]
