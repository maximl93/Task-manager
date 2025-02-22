FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -yq make unzip

COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .

RUN ./gradlew --no-daemon dependencies

COPY src src

RUN ./gradlew installShadowDist

CMD java -jar ./build/libs/demo-0.0.1-SNAPSHOT-all.jar