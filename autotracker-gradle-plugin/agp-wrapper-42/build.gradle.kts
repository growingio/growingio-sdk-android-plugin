plugins {
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(project(":agp-wrapper"))
    implementation(gradleApi())
    compileOnly("com.android.tools.build:gradle:4.2.2")
}