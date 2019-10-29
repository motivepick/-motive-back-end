#!/usr/bin/env bash

PID_FILE=./application.pid

if [[ -f "$PID_FILE" ]]; then
  kill "$(cat ${PID_FILE})"
fi

# nohup ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8188" >/dev/null 2>&1 &
nohup ./mvnw spring-boot:run >/dev/null 2>&1 &
