plugins {
    kotlin("jvm") version "1.6.10"
    `java-gradle-plugin`
    id("io.codearte.nexus-staging") version("0.30.0")
}

repositories {
    google()
    mavenLocal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("autotracker") {
            id = "com.growingio.android.autotracker"
            implementationClass = "com.growingio.android.plugin.AutoTrackerPlugin"
        }
    }
}

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
val testPluginImplementation: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    extendsFrom(configurations["testImplementation"])
}

ext {
    set("releaseVersion", "3.4.0-SNAPSHOT")
    set("releaseVersionCode", 30400)
}

dependencies {
    implementation(gradleApi())

    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-util:9.2")
    implementation("org.ow2.asm:asm-commons:9.2")

    compileOnly(kotlin("stdlib"))
    compileOnly("com.android.tools.build:gradle-api:7.2.0")
    compileOnly("com.android.tools.build:gradle:4.2.2")

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("com.android.tools.build:gradle:4.2.2")

    testPluginImplementation("com.android.tools.build:gradle:4.2.2")
    testPluginImplementation("com.google.guava:guava:30.1.1-jre")
}

tasks.withType(PluginUnderTestMetadata::class.java).named("pluginUnderTestMetadata").configure {
    pluginClasspath.from(testPluginImplementation)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
// 调试 plugin
// 1. 配置 gradle.properties ,设置host端口
// 2. 放断点
// 3. 新建 remote JVM debug ,运行 debug
// 4. 运行构建，开始调试 ./gradlew assembleDebug

apply("publishMaven.gradle")
apply("stagingMaven.gradle")
