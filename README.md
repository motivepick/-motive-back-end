# Motive Back End [![Build Status](https://travis-ci.com/motivepick/motive-back-end.svg?branch=master)](https://travis-ci.com/motivepick/motive-back-end)

The service that is going to defeat the laziness.

Try it out on [https://motivepick.com](https://motivepick.com).

## How To Run Locally

### V1. Install DB on host machine

1. Install PostgreSQL
2. Create the database using `CREATE DATABASE motive ENCODING 'UTF8' TEMPLATE template0;`
3. Make sure that the database is running on `localhost` on `5432` port, the username is `postgres` and the password
   is `postgres`. If that's not the case, see "How To Run With Non-Default Database".
3. In the project root run `./mvnw spring-boot:run -D spring.profiles.active=dev`.

### V2. Run DB in Docker

1. Install Docker
2. Run in project directory:

```
docker build -t db-image .
docker run -d --name db -p 5432:5432 db-image
```

3. Verify container is running:

```
docker ps -a
```

4. Update Spring Boot configuration:

```
spring.datasource.url=jdbc:postgresql://host.docker.internal:5432/motive
```

### MacOS: How To Run Locally

```
brew install postgresql
brew services start postgresql
createuser -s postgres
createdb motive --encoding='utf-8' --template='template0;'
./mvnw spring-boot:run -D spring.profiles.active=dev
```

## Short Note About Deployment To LIVE

When deploy to LIVE make sure to replace `application.yml` with one for LIVE.

## How To Run With Non-Default Database

Add the next parameters to your Spring Boot configuration:

```
spring.datasource.url
spring.datasource.username
spring.datasource.password
```

as on the following screenshot:

![Spring Boot Config](springboot_local_config.png)

## Build Docker Image

```shell
docker image build -t yaskovdev/motive-back-end .
docker image push yaskovdev/motive-back-end
```

## How To Open Swagger

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
