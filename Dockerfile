FROM openjdk:8-jdk as build

WORKDIR /srv

COPY gradlew gradlew.bat ./

COPY gradle/ gradle/

RUN ./gradlew

COPY settings.gradle build.gradle gradle.properties ./

COPY src/ src/

RUN ./gradlew shadowJar && rm gradle.properties

FROM openjdk:8-jre

RUN useradd -m app
USER app

WORKDIR /srv

COPY --from=build --chown=app /srv/build/libs/*-all.jar ./app.jar

COPY src/main/resources/application.properties ./

EXPOSE 8080

CMD java -jar -Djava.security.egd=file:/dev/./urandom -Dserver.port=$PORT app.jar server



