#!/bin/bash

echo "准备开始打包 autotracker-gradle-plugin ..."
cd buildSrc
./../gradlew -Ppublish publishToMavenLocal