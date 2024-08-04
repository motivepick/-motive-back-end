# Motive Back End [![Build Status](https://travis-ci.com/motivepick/motive-back-end.svg?branch=master)](https://travis-ci.com/motivepick/motive-back-end)

The service that is going to defeat the laziness.

Try it out on [https://motivepick.com](https://motivepick.com).

## How To Run Locally

### Configure PostgreSQL Database Locally

Pick one of the below options.

#### Option 1 (For Any OS): PostgreSQL Database In Docker

Based on [the official PostgreSQL](https://github.com/docker-library/docs/blob/master/postgres/README.md) guide.

1. Install Docker.
2. Run the database container:
   ```
   docker run --name db -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=motive -d postgres
   ```
3. Verify container is running:
   ```
   docker ps -a
   ```

#### Option 2 (For macOS): PostgreSQL Database As A Package

```shell
brew install postgresql
brew services start postgresql
createuser -s postgres
createdb motive --encoding='utf-8' --template='template0;'
```

### Start The Back End

In the project root run `./mvnw spring-boot:run -D spring.profiles.active=local`.

Or, if you are using IntelliJ IDEA, make sure that "Active profiles" is set to `local` and run from the IDE.


## How To Run Locally With A Non-Default Database

Add the next parameters to your Spring Boot configuration:

```
spring.datasource.url
spring.datasource.username
spring.datasource.password
```

as on the following screenshot:

![Spring Boot Config](springboot_local_config.png)

## How To Deploy To Azure App Service

Make sure that `subscriptionId` in `pom.xml` is your active Azure subscription.

```powershell
mvn clean package
mvn azure-webapp:deploy
```

## Short Note About Deployment To LIVE

When deploy to LIVE make sure to replace `application.yml` with one for LIVE.

## Build Docker Image

```shell
docker image build -t yaskovdev/motive-back-end .
docker image push yaskovdev/motive-back-end
```

## How To Open Swagger

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
