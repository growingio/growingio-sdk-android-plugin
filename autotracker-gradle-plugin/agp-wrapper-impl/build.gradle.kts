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
    implementation(project(":agp-wrapper-72"))
    implementation(project(":agp-wrapper-81"))
    implementation(gradleApi())

    compileOnly("org.ow2.asm:asm:9.5")
    compileOnly("org.ow2.asm:asm-commons:9.5")
    compileOnly("com.android.tools.build:gradle-api:${rootProject.extra["agp_version"]}")
}