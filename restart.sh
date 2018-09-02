#!/usr/bin/env bash

PID_FILE=./application.pid
JAR_PATH=target/motive-back-end-0.0.1-SNAPSHOT.jar
SECURE_PATH=../secure.yml

if [ -f "$PID_FILE" ]
then
    kill $(cat ${PID_FILE})
fi

nohup java -jar -Dspring.profiles.active=prod $JAR_PATH --spring.config.location=classpath:application.yml,classpath:application-prod.yml,file:$SECURE_PATH > /dev/null 2>&1 &