import java.lang.System.getProperty

plugins {
    kotlin("jvm") version "1.6.10"
    `java-gradle-plugin`
}

repositories {
    google()
    mavenLocal()
    mavenCentral()
}

//compileKotlin {
//    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8
//}

// 将插件的版本放入 jar 包中
//tasks.jar {
//    manifest {
//        attributes(
//            mapOf(
//                "Manifest-Version" to "1.0",
//                "Gradle-Plugin-Version" to "4.0.0"
//            )
//        )
//    }
//}

ext {
    set("releaseVersion", "3.4.0")
    set("releaseVersionCode", 30400)
}

dependencies {
//    implementation("com.android.tools.build:gradle-api:7.2.0-beta04")
    implementation(gradleApi())


    // 打包时不需要依赖 gradle-api，但项目运行时则需要
    if (!hasProperty("publish")) {
        implementation("com.android.tools.build:gradle-api:7.2.0-beta04")
    } else {
        compileOnly("com.android.tools.build:gradle-api:7.2.0-beta04")
    }

    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-util:9.2")
    implementation("org.ow2.asm:asm-commons:9.2")

    compileOnly(kotlin("stdlib"))
    compileOnly("com.android.tools.build:gradle:4.2.2")


    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.3")
}
// 调试 plugin
// 1. 配置 gradle.properties ,设置host端口
// 2. 运行 $ ./gradlew --stop     # 先停掉 daemon 进程
//         $ ./gradlew --daemon   # 启动 daemon 进程
// 3. Attach daemon 进程  run -> Attach to Process 选择进程
// 4. 放断点
// 5. 新建 remote JVM debug ,运行 debug
// 6. 运行构建，开始调试 ./gradlew assembleDebu

apply("publishMaven.gradle")