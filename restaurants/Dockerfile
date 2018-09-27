FROM aurelpaulovic/scala-sbt:2.12.6-0.13.16 AS build

WORKDIR /root/app

COPY project project
COPY build.sbt build.sbt
COPY src src

RUN sbt assembly && \
    mv target/scala-2.12/restaurants.jar /restaurants.jar

FROM openjdk:8

COPY --from=build /restaurants.jar /restaurants.jar

EXPOSE 8888
EXPOSE 9990

CMD ["java", "-jar", "/restaurants.jar"]
