/*
 *   Copyright (c) 2022 Beijing Yishu Technology Co., Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies{
    implementation(kotlin("stdlib"))
    implementation(project(":inject-annotation"))

    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.20-1.0.6")
    // https://square.github.io/kotlinpoet/
    implementation("com.squareup:kotlinpoet:1.12.0")
}

// 运行 $ ./gradlew --stop     # 先停掉 daemon 进程
// $ ./gradlew --daemon   # 启动 daemon 进程