import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("kapt") version "2.1.20"
    id("distribution")
    id("com.vanniktech.maven.publish") version "0.29.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

ktlint {
    debug.set(false)
    verbose.set(true)
    android.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    enableExperimentalRules.set(true)
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

val sep = File.separator
distributions {
    main {
        contents {
            from("build${sep}libs")
            from("build${sep}publications${sep}maven")
        }
    }
}

mavenPublishing {
    coordinates("com.growingio", "growingio-compose-plugin", "1.0.0")

    pom {
        name.set("growingio-compose-plugin")
        description.set("GrowingIO SDK Compose Kotlin Compile Plugin.")
        url.set("https://github.com/growingio/growingio-sdk-android-plugin")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("growingio")
                name.set("GrowingIO SDK Team.")
                url.set("https://github.com/growingio/")
            }
        }
        scm {
            url.set("https://github.com/growingio/growingio-sdk-android-plugin")
            connection.set("scm:git@github.com:growingio/growingio-sdk-android-plugin.git")
            developerConnection.set("scm:git@github.com:growingio/growingio-sdk-android-plugin.git")
        }
    }

    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()

    // How to publish
    // 1. set mavenCentralUsername=<> mavenCentralPassword=<> in your gradle.properties
    // 2. ./gradlew :growingio-compose-plugin:publishToMavenCentral
}

tasks.named("distZip") {
    dependsOn("publishToMavenLocal")
    onlyIf {
        inputs.sourceFiles.isEmpty.not().also {
            require(it) { "No distribution to zip." }
        }
    }
}

repositories {
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")

    kapt("com.google.auto.service:auto-service:1.0.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")

    testImplementation(kotlin("test-junit"))
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.6.0")
    testImplementation("org.jetbrains.compose.desktop:desktop:1.6.10")
}

kapt {
    correctErrorTypes = true
}