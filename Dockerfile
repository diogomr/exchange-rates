FROM adoptopenjdk/openjdk11:debian-slim as BUILD_IMAGE

ENV APP_HOME /app
RUN mkdir $APP_HOME
WORKDIR $APP_HOME

ADD gradlew $APP_HOME
ADD gradle $APP_HOME/gradle
ADD gradle.properties $APP_HOME/gradle.properties
ADD build.gradle.kts $APP_HOME
ADD settings.gradle.kts $APP_HOME

ADD src $APP_HOME/src

RUN $APP_HOME/gradlew clean build test

FROM adoptopenjdk/openjdk11:debian-slim
WORKDIR /app

RUN apt update && apt install -y procps && rm -rf /var/lib/apt/lists/*
COPY --from=BUILD_IMAGE /app/build/libs/exchange-rates.jar .
ENTRYPOINT ["java","-jar","/app/exchange-rates.jar"]

