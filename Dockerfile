FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -yq make unzip

COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .

RUN ./gradlew --no-daemon dependencies

COPY lombok.config .
COPY src src

RUN ./gradlew installShadowDist

CMD java -jar ./build/libs/app-1.0-SNAPSHOT-all.jar