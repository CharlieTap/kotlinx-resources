import com.goncalossilva.useanybrowser.useAnyBrowser
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    alias(libs.plugins.kotlin.multiplatform)

    alias(libs.plugins.useanybrowser)

    alias(libs.plugins.detekt)
}

buildscript {
    dependencies {
        classpath("com.goncalossilva:resources")
    }
}
apply(plugin = "com.goncalossilva.resources")

plugins.withType<NodeJsRootPlugin> {
    configure<NodeJsRootExtension> {
        nodeVersion = libs.versions.nodejs.get()
    }
}

plugins.withType<YarnPlugin> {
    configure<YarnRootExtension> {
        version = libs.versions.yarn.get()
        yarnLockMismatchReport = YarnLockMismatchReport.WARNING
        yarnLockAutoReplace = true
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        browser {
            testTask {
                useKarma {
                    useAnyBrowser()
                }
            }
        }
        nodejs()
    }

    ios()
    iosSimulatorArm64()
    watchos()
    watchosSimulatorArm64()
    tvos()
    tvosSimulatorArm64()

    mingwX64()
    macosX64()
    macosArm64()
    linuxX64()
    linuxArm64()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.goncalossilva:resources-library")
            }
        }
    }
}

detekt {
    config.setFrom(files("../config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}
