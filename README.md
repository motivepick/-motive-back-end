# Motive Back End [![Build Status](https://travis-ci.com/motivepick/motive-back-end.svg?branch=master)](https://travis-ci.com/motivepick/motive-back-end)

The service that is going to defeat the laziness.

Try it out on [https://milestone.yaskovdev.com](https://milestone.yaskovdev.com).

## How To Run Locally

### Configure PostgreSQL Database Locally

Pick one of the below options.

#### Option 1 (For Any OS): PostgreSQL Database In Docker

Based on [the official PostgreSQL](https://github.com/docker-library/docs/blob/master/postgres/README.md) guide.

1. Install Docker.
2. Run the database container:
   ```
   docker run --name db -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=motive -d --restart unless-stopped postgres
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

## How To Run In A Local Network

It is useful if you want to, say, debug a mobile client against the back end that is running on a local computer.

1. Configure port forwarding for ports `3000` and `8080`. They should be forwarded to your local computer where the back
   end and the Web app will be running. You can find the IP address of the computer using the `ipconfig` command. It
   usually starts with `192.168`.
2. Create a DNS `A` record for your domain (assuming your domain is `yaskovdev.com`). The record should map
   both `milestone-local.yaskovdev.com` and `api.milestone-local.yaskovdev.com` to the same IP address: a public IP
   address of your local network. You can find it [here](https://www.whatismyip.com/).
3. Wait for some time for the DNS record to become visible everywhere.
4. Make sure that both your local computer and your phone are connected to your local network (local router).
5. In `application-local.yml` replace the value of `authentication.success.url.web`
   with `http://milestone-local.yaskovdev.com:3000`.
6. In `application-local.yml` replace the value of `logout.success.url`
   with `http://milestone-local.yaskovdev.com:3000/login`.
7. In `application-local.yml` replace the value of `cookie.domain` with `milestone-local.yaskovdev.com`.
8. In the `motive-web-app`, in the `.env.development` file, replace the value of `REACT_APP_API_URL`
   with `http://api.milestone-local.yaskovdev.com:8080`.
9. Run both the back end and the Web app.
10. Make sure the back end is running and accessible by
    opening http://api.milestone-local.yaskovdev.com:8080/actuator/health.
11. Make sure the Web app is running and accessible by opening http://api.milestone-local.yaskovdev.com:3000.
12. In your phone open http://milestone-local.yaskovdev.com:3000.

## How To Deploy To Prod

### Deploy The Back End To Azure App Service

Based
on [this guide](https://learn.microsoft.com/en-us/azure/app-service/quickstart-java?tabs=springboot&pivots=java-javase).

Make sure that `subscriptionId` in `pom.xml` is your active Azure subscription.

Make sure to have `application-prod.yml` next to `application.yml`.

Run the next commands:

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

## How To Check The Status Of The Back End

For Local, go to `http://localhost:8080/actuator/health`.

For Prod, go to `https://motive-back-end.azurewebsites.net/actuator/health`.

## How To Open Swagger Locally

Use [this link](http://localhost:8080/swagger-ui/index.html).

## Troubleshooting

1. If you cannot connect from your local computer to the PostgreSQL database deployed to Azure, make sure that your IP
   address is added to the "Firewall rule name" in the "Networking" page in the "Azure Database for PostgreSQL flexible
   server".
2. If you press "Try Without Login" in the UI and get redirected back to the login page, make sure that
   the `MOTIVE_SESSION` cookie is set (most likely it isn't, therefore you get redirected back to the login).
