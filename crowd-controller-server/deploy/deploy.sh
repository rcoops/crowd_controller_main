#!/bin/bash
echo 'DEPLOYING SCRIPTS'
sftp stb098@helios.csesalford.com <<< $'put deploy/st*.sh'
echo 'SCRIPT DEPLOYMENT END'
echo 'SHUTTING DOWN APP'
curl -X POST http://stb098.edu.csesalford.com/actuator/shutdown
echo 'SHUTDOWN END'
echo 'REMOVING OLD JAR'
ssh stb098@helios.csesalford.com <<< $'bash stop.sh'
echo 'OLD JAR REMOVAL END'
echo 'DEPLOYING NEW JAR'
sftp stb098@helios.csesalford.com <<< $'put target/crowd-controller-server-*.jar'
echo 'JAR DEPLOYMENT END'
echo 'STARTING APP'
ssh stb098@helios.csesalford.com <<< $'bash start.sh'
echo 'APP START END'
