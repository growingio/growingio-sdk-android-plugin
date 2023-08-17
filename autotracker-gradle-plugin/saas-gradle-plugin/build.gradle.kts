plugins {
    id("org.jetbrains.kotlin.jvm")
    `java-gradle-plugin`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

val shadowed: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}
configurations {
    shadowed
    compileOnly {
        extendsFrom(shadowed)
    }
    testImplementation{
        extendsFrom(shadowed)
    }
}

dependencies {
    shadowed(project(":agp-wrapper-impl"))
    compileOnly(project(":agp-wrapper-42"))

    implementation(gradleApi())
    compileOnly("com.android.tools.build:gradle-api:${rootProject.extra["agp_version"]}")
    compileOnly("com.android.tools.build:gradle:${rootProject.extra["low_agp_version"]}")
}

tasks.jar {
    val dependencies = shadowed.filter {
        it.name.startsWith("agp-")
    }.map { zipTree(it) }
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}