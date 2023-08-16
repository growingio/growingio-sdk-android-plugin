buildscript {
    extra.apply {
        set("kotlin_version", "1.8.20")
        set("agp_version", "8.1.0")
        set("low_agp_version", "4.2.2")
        set("releaseVersion", "3.5.0")
        set("releaseVersionCode", 30500)
    }
}

plugins {
    kotlin("jvm") version ("1.8.20")
    `java-gradle-plugin`

    id("com.gradle.plugin-publish") version ("1.2.0")
}

group = providers.gradleProperty("GROUP").get()
version = project.extra["releaseVersion"] as String

//./gradlew publishPlugins --validate-only
//./gradlew publishPlugins
@Suppress("UnstableApiUsage")
gradlePlugin {
    website.set(providers.gradleProperty("POM_OFFICIAL_WEBSITE").get())
    vcsUrl.set(providers.gradleProperty("POM_SCM_URL").get())
    plugins {
        create("autotracker") {
            id = "com.growingio.android.autotracker"
            implementationClass = "com.growingio.android.plugin.AutoTrackerPlugin"
            description = providers.gradleProperty("POM_DESCRIPTION").get()
            tags.set(listOf("growingio", "autotracker", "plugin"))
        }
    }
}

val testPluginImplementation: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    extendsFrom(configurations.testImplementation.get())
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

    //implementation(project(":growingio-plugin-library"))
    shadowed(project(":agp-wrapper-impl"))
    compileOnly(project(":agp-wrapper-42"))

    implementation(gradleApi())

    implementation("org.ow2.asm:asm:9.2")
//    implementation("org.ow2.asm:asm-util:9.2")
//    implementation("org.ow2.asm:asm-commons:9.2")

//    compileOnly(kotlin("stdlib"))
    compileOnly("com.android.tools.build:gradle-api:${rootProject.extra("agp_version")}")
    compileOnly("com.android.tools.build:gradle:${rootProject.extra("low_agp_version")}")

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("com.android.tools.build:gradle:${rootProject.extra("low_agp_version")}")

    testPluginImplementation("com.android.tools.build:gradle:${rootProject.extra("low_agp_version")}")
    testPluginImplementation("com.google.guava:guava:30.1.1-jre")
}

tasks.withType(PluginUnderTestMetadata::class.java).named("pluginUnderTestMetadata").configure {
    pluginClasspath.from(testPluginImplementation)
}


java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.jar {
    val dependencies = shadowed.filter {
        it.name.startsWith("agp-")
    }.map { zipTree(it) }
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}


// 调试 plugin
// 1. 配置 gradle.properties ,设置host端口
// 2. 放断点
// 3. 新建 remote JVM debug ,运行 debug
// 4. 运行构建，开始调试 ./gradlew assembleDebug

//apply("publishMavenWithPluginMarker.gradle")
//apply("stagingMaven.gradle")
