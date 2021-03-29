#!/bin/sh

JAR_FILE=google-photo-manager.jar
CURRENT_DIR=$PWD

java -Dcom.sun.management.jmxremote=true \
	-Dcom.sun.management.jmxremote.port=5000 \
	-Dcom.sun.management.jmxremote.authenticate=false \
	-Dcom.sun.management.jmxremote.ssl=false \
	-Dcom.sun.management.jmxremote.local.only=false \
	-jar $JAR_FILE &