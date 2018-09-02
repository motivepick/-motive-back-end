#!/usr/bin/env bash

PID_FILE=./application.pid
JAR_PATH=target/motive-back-end-0.0.1-SNAPSHOT.jar

if [ -f "$PID_FILE" ]
then
    kill $(cat ${PID_FILE})
fi

nohup java -Dspring.config.location=file:./../secure.yml,src/main/resources/application.yml -jar -Dspring.profiles.active=prod $JAR_PATH > /dev/null 2>&1 &