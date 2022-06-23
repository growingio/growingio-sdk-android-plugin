plugins {
    kotlin("jvm") version "1.6.21"
    `java-gradle-plugin`

    id("io.codearte.nexus-staging") version ("0.30.0")
}

gradlePlugin {
    plugins {
        create("autotracker") {
            id = "com.growingio.android.autotracker"
            implementationClass = "com.growingio.android.plugin.AutoTrackerPlugin"
        }
    }
}

val testPluginImplementation: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    extendsFrom(configurations.testImplementation.get())
}

ext {
    set("releaseVersion", "3.4.0")
    set("releaseVersionCode", 30400)
    set("agp_version", "7.2.1")
    set("low_agp_version", "4.2.2")
    set("kotlin_version", "1.6.21")
}

dependencies {

    implementation(project(":growingio-plugin-library"))
    compileOnly(project(":agp-wrapper-impl"))

    implementation(gradleApi())

    implementation("org.ow2.asm:asm:9.2")
//    implementation("org.ow2.asm:asm-util:9.2")
//    implementation("org.ow2.asm:asm-commons:9.2")

//    compileOnly(kotlin("stdlib"))
    compileOnly("com.android.tools.build:gradle-api:${rootProject.ext.get("agp_version")}")
    compileOnly("com.android.tools.build:gradle:${rootProject.ext.get("low_agp_version")}")

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("com.android.tools.build:gradle:${rootProject.ext.get("low_agp_version")}")

    testPluginImplementation("com.android.tools.build:gradle:${rootProject.ext.get("low_agp_version")}")
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

apply("publishMavenWithPluginMarker.gradle")
apply("stagingMaven.gradle")
