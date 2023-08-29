#!/bin/bash

echo "packaging autotracker-gradle-plugin ..."
./gradlew :autotracker-gradle-plugin:clean \
&& ./gradlew :autotracker-gradle-plugin:saas-gradle-plugin:publishToMavenLocal
# ./gradlew :autotracker-gradle-plugin:publishToMavenLocal
