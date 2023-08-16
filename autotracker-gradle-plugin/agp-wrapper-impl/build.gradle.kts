plugins {
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    api(project(":agp-wrapper"))
    implementation(project(":agp-wrapper-42"))
    implementation(project(":agp-wrapper-70"))
    implementation(project(":agp-wrapper-71"))
    implementation(project(":agp-wrapper-72"))
    implementation(project(":agp-wrapper-81"))
    implementation(gradleApi())
    compileOnly("com.android.tools.build:gradle-api:${rootProject.extra("kotlin_version")}")
}