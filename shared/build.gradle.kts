plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("maven-publish")
    id("signing")
    kotlin("plugin.serialization")
}

group = "io.github.succlz123"
version = "1.0.5"

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    android {
        publishLibraryVariants("release")
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)

                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                api("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")

                api("io.ktor:ktor-client-core:2.1.1")
//                api("io.ktor:ktor-client-cio:2.1.1")
                api("io.ktor:ktor-client-logging:2.1.1")
                api("io.ktor:ktor-client-content-negotiation:2.1.1")
                api("io.ktor:ktor-serialization-kotlinx-json:2.1.1")
                api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.3")
                api("io.ktor:ktor-client-okhttp:2.1.1")

                api("cn.wanghaomiao:JsoupXpath:2.2")
                api("org.jsoup:jsoup:1.13.1")

                api("com.russhwolf:multiplatform-settings:1.0.0-RC")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.5.1")
                api("androidx.core:core-ktx:1.9.0")

                api("io.coil-kt:coil-compose:2.2.1")
                api("io.github.succlz123:compose-screen-android:0.0.3")
                api("com.github.succlz123:HoHoPlayer:0.1.6")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                api("io.github.succlz123:compose-screen-desktop:0.0.3")
                api("io.github.succlz123:compose-imageloader-desktop:0.0.2")

                api("uk.co.caprica:vlcj:4.7.3")
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].assets.setSrcDirs(listOf("${project.rootDir}/shared/src/commonMain/resources"))
    defaultConfig {
        minSdk = 21
        targetSdk = 30
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
