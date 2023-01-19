# TTP task

Author: Aleksei Mikhalev amseager@mail.ru

## OpenAPI Specification

Schemas and endpoints follow OpenAPI 3 and can be found [here](./src/main/resources/static/v3/api-docs.yml).

## JRE version

17

## Local Run

<b>API dto and controller interfaces need to be generated from OpenAPI specification first.</b>
Please run `mvn package` once before using the app.

All the properties from [application.yml](./src/main/resources/application.yml) can be easily configured by using
the [Spring Boot relaxed binding feature](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties.relaxed-binding).

## App Usage

After launching the app, the Swagger page can be found at `{app_url}/swagger-ui.html`.

The task requires making http calls to some external vendor. 
Either set the appropriate root URL via `external.vendor.root-uri` property or use stub responses by setting `external.vendor.use-stubs=true`.

In order to tune the retry policy for external calls, use `external.vendor.max-attempts` and `external.vendor.retry-time-interval` (not for the "stub" mode).

To set the currency value for the conversion API, use `app-configs.currency` property ("EUR" by default).

## Database Usage

Liquibase is used for applying database migrations. The scripts can be found [here](./src/main/resources/db/changelog/db.changelog-master.xml).

Spring Data Jpa (with Hibernate) is used for working with a database at runtime. To check the date stored in a database, use any DB client that you prefer.

## Tests

The app contains both unit (JUnit) and integration (JUnit + Spring Boot test + Testcontainers + WireMock) tests.

The unit tests are bound to `mvn test` goal.

The integration tests are bound to `mvn verify` goal, also they require having Docker environment in order to be launched. They use the stubs provided in the task among others.