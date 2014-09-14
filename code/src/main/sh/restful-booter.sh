#!/bin/bash

java -cp "./conf/:lib/*" $JAVA_OPTS -Dlog4j.configuration=log4j.properties org.apache.deltaspike.example.se.Startup