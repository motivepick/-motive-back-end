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

## How To Deploy To Prod

### Deploy The Back End To Azure App Service

Based
on [this guide](https://learn.microsoft.com/en-us/azure/app-service/quickstart-java?tabs=springboot&pivots=java-javase).

Make sure that `subscriptionId` in `pom.xml` is your active Azure subscription.

```powershell
mvn clean package
mvn azure-webapp:deploy
```

### Configure A Custom Domain

Use [this guide](https://learn.microsoft.com/en-gb/azure/app-service/app-service-web-tutorial-custom-domain?tabs=root%2Cazurecli).

### Deploy A Database

Use [this guide](https://learn.microsoft.com/en-us/azure/developer/java/spring-framework/configure-spring-data-jdbc-with-azure-postgresql).

## Short Note About Deployment To LIVE

When deploy to LIVE make sure to replace `application.yml` with one for LIVE.

## Build Docker Image

```shell
docker image build -t yaskovdev/motive-back-end .
docker image push yaskovdev/motive-back-end
```

## How To Open Swagger

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Troubleshooting

1. If you cannot connect from your local computer to the PostgreSQL database deployed to Azure, make sure that your IP
   address is added to the "Firewall rule name" in the "Networking" page in the "Azure Database for PostgreSQL flexible
   server".
2. If you press "Try Without Login" in the UI and get redirected back to the login page, make sure that
   the `MOTIVE_SESSION` cookie is set (most likely it isn't, therefore you get redirected back to the login).
