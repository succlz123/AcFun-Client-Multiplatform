plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

group = "io.github.succlz123"
version = "1.0.5"

repositories {
    mavenCentral()
    google()
    google()
    maven("https://jitpack.io")
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.activity:activity-compose:1.6.0")
    implementation("androidx.compose.material:material:1.2.1")
    implementation("androidx.compose.foundation:foundation:1.2.1")
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "org.succlz123.app.acfun"
        minSdk = 21
        targetSdk = 30
        versionCode = 5
        versionName = "1.0.5"

        resourceConfigurations += mutableSetOf("en", "zh")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("${project.rootDir}/security/android-debug.keystore")
            storePassword = "android"
            keyAlias = "key"
            keyPassword = "android"
        }
        register("release") {
            storeFile = file("${project.rootDir}/security/android-debug.keystore")
            storePassword = "android"
            keyAlias = "key"
            keyPassword = "android"
        }
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = false
            isDebuggable = false
            ndk {
                abiFilters += "arm64-v8a"
                abiFilters += "armeabi-v7a"
            }
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }
}