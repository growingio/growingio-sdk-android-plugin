plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.growingio.android.descriptor"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        jvmToolchain(11)
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.6.1")

    compileOnly(files("libs/uc_webview_sdk-2.14.0.31.jar"))
    compileOnly(files("libs/tbs_sdk_44051.jar"))

    implementation(project(":inject-annotation"))
    ksp(project(":inject-annotation:processor"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}


tasks.register<Copy>("copyHookClass") {
    from("build/generated/ksp/debug/kotlin/com/growingio/android/plugin/hook/")
    into("../autotracker-gradle-plugin/agp-wrapper-impl/src/main/kotlin/com/growingio/android/plugin/hook/")
}
tasks.named("build") {
    finalizedBy("copyHookClass")
}