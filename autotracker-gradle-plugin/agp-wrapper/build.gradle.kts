plugins {
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

//kotlin {
//    compilerOptions {
//        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
//    }
//}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(gradleApi())
    compileOnly("com.android.tools.build:gradle-api:${rootProject.extra["agp_version"]}")
}