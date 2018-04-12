#!/bin/bash

echo 'SHUTTING DOWN APP'
ssh rick@crowdcontroller.ddns.net <<< $'sudo service crowdcontroller stop'
echo 'SHUTDOWN END'
echo 'REMOVING OLD JAR'
ssh rick@crowdcontroller.ddns.net <<< $'rm /var/crowdcontroller/crowd-controller-server.jar'
echo 'OLD JAR REMOVAL END'
echo 'DEPLOYING NEW JAR'
sftp rick@crowdcontroller.ddns.net <<< $'put target/crowd-controller-server-*.jar /var/crowdcontroller/crowd-controller-server.jar'
echo 'JAR DEPLOYMENT END'
echo 'STARTING APP'
ssh rick@crowdcontroller.ddns.net <<< $'sudo service crowdcontroller start'
echo 'APP START END'
