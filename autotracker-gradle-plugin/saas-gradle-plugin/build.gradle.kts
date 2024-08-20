buildscript {
    extra.apply {
        set("saasVersion", "2.10.3-SNAPSHOT")
        set("saasVersionCode", "21003")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

group = providers.gradleProperty("GROUP").get()
version = project.extra["saasVersion"] as String

@Suppress("UnstableApiUsage")
gradlePlugin {
    website.set(providers.gradleProperty("POM_OFFICIAL_WEBSITE").get())
    vcsUrl.set(providers.gradleProperty("POM_SCM_URL").get())
    plugins {
        create("saas") {
            id = "com.growingio.android.saas"
            displayName = providers.gradleProperty("POM_NAME").get()
            implementationClass = "com.growingio.android.plugin.SaasAutoTrackerPlugin"
            description = providers.gradleProperty("POM_DESCRIPTION").get()
            tags.set(listOf("growingio", "autotracker", "saas"))
        }
    }
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
    }.map {
        zipTree(it)
    }
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

//apply("publishMavenWithPluginMarker.gradle")