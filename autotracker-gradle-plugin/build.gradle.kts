buildscript {
    extra.apply {
        set("kotlin_version", "2.1.20")
        set("agp_version", "8.9.1")
        set("low_agp_version", "4.2.2")
        set("releaseVersion", "4.4.2-SNAPSHOT")
        set("releaseVersionCode", 40402)
        set("composeVersion", "1.0.0")
    }
}

plugins {
    kotlin("jvm") version ("2.1.20")
    `java-gradle-plugin`

    id("com.gradle.plugin-publish") version ("1.2.0")
    id("com.github.gmazzo.buildconfig") version ("3.1.0")
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
            displayName = providers.gradleProperty("POM_NAME").get()
            implementationClass = "com.growingio.android.plugin.AutoTrackerPlugin"
            description = providers.gradleProperty("POM_DESCRIPTION").get()
            tags.set(listOf("growingio", "autotracker", "agp8"))
        }

        create("compose") {
            id = "com.growingio.compose.plugin"
            displayName = providers.gradleProperty("POM_KOTLIN_NAME").get()
            description = providers.gradleProperty("POM_KOTLIN_DESCRIPTION").get()
            implementationClass = "com.growingio.compose.plugin.GrowingKotlinCompilerGradlePlugin"
            tags.set(listOf("growingio", "compose", "kotlin compiler plugin"))
        }
    }
}

// provide by "com.github.gmazzo.buildconfig" plugin
buildConfig {
    packageName("com.growingio")
    className("BuildConfig")

    buildConfigField("String", "Version", provider { "\"${project.extra["composeVersion"]}\"" })
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
    testImplementation {
        extendsFrom(shadowed)
    }
}


dependencies {

    shadowed(project(":agp-wrapper-impl"))
    compileOnly(project(":agp-wrapper-42"))

    implementation(gradleApi())

    compileOnly("org.ow2.asm:asm:9.5")
//    implementation("org.ow2.asm:asm-util:9.2")
//    implementation("org.ow2.asm:asm-commons:9.2")

//    compileOnly(kotlin("stdlib"))
    compileOnly("com.android.tools.build:gradle-api:${rootProject.extra["agp_version"]}")
    compileOnly("com.android.tools.build:gradle:${rootProject.extra["low_agp_version"]}")

    // kotlin compiler plugin
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.20")

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("org.ow2.asm:asm:9.5")
    testImplementation("org.ow2.asm:asm-commons:9.5")
    testImplementation("com.android.tools.build:gradle:8.9.1")
    testImplementation(project(":agp-wrapper-72"))

    testPluginImplementation("com.android.tools.build:gradle:8.9.1")
    testPluginImplementation("com.google.guava:guava:33.3.1-jre")
}

tasks.withType(PluginUnderTestMetadata::class.java).named("pluginUnderTestMetadata").configure {
    pluginClasspath.from(testPluginImplementation)
}


java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    jvmToolchain(11)
}

testing {
    kotlin {
        jvmToolchain(17)
    }
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

tasks.clean {
    subprojects {
        delete(this.getLayout().getBuildDirectory())
    }
}


// 调试 plugin
// 1. 配置 gradle.properties ,设置host端口
// 2. 放断点
// 3. 新建 remote JVM debug ,运行 debug
// 4. 运行构建，开始调试 ./gradlew assembleDebug
// apply("publishMavenWithPluginMarker.gradle")

// fix sign plugin error
// val signingTasks = tasks.withType<Sign>()
// tasks.withType<AbstractPublishToMaven>().configureEach {
//     mustRunAfter(signingTasks)
// }