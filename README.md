# Air quality monitoring API written in Kotlin  🛠


## Purpose

The project is a polygon for learning the art of building and testing an API.

I used the well-known ope service [LUCHTMEETNET 2020 OPENAPI](https://api-docs.luchtmeetnet.nl/).

## Info from Luchtmeetnet

The website Luchtmeetnet is an initiative of the Ministry of Infrastructure and the Environment, 
the National Institute of Public Health and the Environment (RIVM), GGD Amsterdam, EPA for the greater Rotterdam area (DCMR), 
the Province of Limburg, EPA's middle and western Brabant (OMWB) and EPA region Arnhem (ODRA). 
The website displays the measured air quality on several measuring stations in the Netherlands and the calculated air quality maps.
This API grants direct access to detailed information of the stations and the measurements made there.

## Motivation 

Luchtmeetnet service is providing paged data over many pollutants measured by many stations every hour. 
My motivation is to aggregate the measurements and provide the snapshots that are helpful if you want some
practical answers about the quality of air you are breathing, 
or if you want to know the trends of air contamination in your region.

## Software Stack

* Java JDK 8+/Kotlin as main execution environment;
* Gradle 6 as main build tool;
* Vert.X Unit and JUnit 4 as testing framework;
* (TODO) Docker is potentially supported if you intend trying out the demo on Mac / Linux 

## Setup

You need to have Java 8 SDK set up.

## IDE

This project was created in IntelliJ Community edition.

## How to run

This project uses [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html).
Switch to the project root directory and use one the following commands.
Build the project:
```
$./gradlew build 
```
Run the Vert.X server in development mode
```
$./gradlew run 
```
## Testing

Each data service is shipped with integration tests.
Current framework of integration tests is built with [Apache Http Client](https://hc.apache.org/httpcomponents-client-4.5.x/index.html), 
[Vert.X Unit](https://vertx.io/docs/vertx-unit/java/) and Junit. 
Integration tests of air quality monitor API are also armed with [Test Retry Gradle Plugin](https://github.com/gradle/test-retry-gradle-plugin).

The synchronous HTTP client is to be replaced either with [REST Assured](http://rest-assured.io/) 
or with [Vert.X Async Http client](https://how-to.vertx.io/http-client-howto/)

Run integration tests using command:

```
$./gradlew test 
```

Build a fat Jar and run in production mode using command
 ```sh
 $./gradlew shadowJar
 $java -jar build/libs/app-shadow.jar
 ``` 

## API ENDPOINTS

Current version of air quality monitoring data service provides the following endpoints:

* `/v1/api/measurements` - Sequences of air pollutants measurements taken from now back to 6 hours
* `/v1/api/measurements/{station}` - Aggregated value (currently max, min, avg, default: avg) of each air pollutant measured from now back to to 6 hours by the {station}
* `/v1/api/measurements/region/{region}` - Aggregated value (currently max, min, avg, default: avg) of each air pollutant measured from now back to to 6 hours in the {region} (zh, rd)
* `/v1/api/components` - List of monitored air pollutants
* `/v1/api/components/{formula}` - Air pollutant details, where {formula} is one of: (O3, FN, NO, NO2, PM10, PM25)
* `/v1/api/components/{formula}/limit` - Air pollutant measurement upper limit, where {formula} is one of: (O3, FN, NO, NO2, PM10, PM25)
* `/v1/api/stations` - List of stations daily monitoring the air quality in the Netherlands
* `/v1/api/stations/{loc}` - List of stations daily monitoring the air quality in the Netherlands having {loc} in location description (amsterdam or ams)


### Included features

* [Kotlin coroutines concurrently querying the paged responses](https://kotlinexpertise.com/kotlin-coroutines-concurrency/)
* [Retrofit2](https://github.com/square/retrofit) (+ [Kotlin coroutines await() for Retrofit2 Call](https://github.com/gildor/kotlin-coroutines-retrofit))
* [Gradle](https://gradle.org/)
* Integration tests with [Vert.X Unit API](https://vertx.io/docs/vertx-unit/java/)
