#!/usr/bin/env bash

export SPRING_DATA_MONGODB_HOST=167.99.40.5
export SPRING_DATA_MONGODB_PORT=27017

PID_FILE=./application.pid

if [ -f "$PID_FILE" ]
then
    kill $(cat ${PID_FILE})
fi

nohup ./mvnw spring-boot:run >/dev/null 2>&1 &
