#!/bin/bash

echo "packaging autotracker-gradle-plugin ..."
./gradlew :autotracker-gradle-plugin:growingio-plugin-library:publishToMavenLocal \
&& ./gradlew :autotracker-gradle-plugin:publishToMavenLocal

# ./gradlew :autotracker-gradle-plugin:growingio-plugin-library:publishToMavenLocal