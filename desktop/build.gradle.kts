import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "io.github.succlz123"
version = "1.0.5"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(compose.desktop.currentOs)
//                implementation(files("${project.projectDir}/disklrucache-2.0.3.jar"))
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            macOS {
                iconFile.set(project.file("ic_acfun.icns"))
            }
            windows {
                iconFile.set(project.file("ic_acfun.ico"))
            }
            linux {
                iconFile.set(project.file("ic_acfun.png"))
            }
            packageName = "AcFun"
            packageVersion = "1.0.5"
            copyright = "Copyright © 2022"

            modules("java.sql", "jdk.unsupported")

            args(project.projectDir.absolutePath)
            buildTypes.release.proguard {
                configurationFiles.from("rules.pro")
            }
        }
    }
}
