#!/bin/bash

echo "packaging autotracker-gradle-plugin ..."
./gradlew :autotracker-gradle-plugin:growingio-plugin-library:publish \
&& ./gradlew :autotracker-gradle-plugin:publishPluginMavenPublicationToMavenRepository