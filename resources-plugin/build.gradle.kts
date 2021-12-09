import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"

    id("com.github.gmazzo.buildconfig") version "3.0.3"

    id("java-gradle-plugin")
    id("maven-publish")
    id("signing")
    id("com.gradle.plugin-publish") version "0.18.0"

    id("io.gitlab.arturbosch.detekt") version "1.19.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("stdlib"))
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.contracts.ExperimentalContracts"
    }
}

val artifactId: String by project
val pluginId = "$group.$artifactId"

ext["gradle.publish.key"] = property("gradlePublishKey") as String
ext["gradle.publish.secret"] = property("gradlePublishSecret") as String

buildConfig {
    packageName.set(pluginId)
    buildConfigField("String", "GROUP_ID", "\"$group\"")
    buildConfigField("String", "ARTIFACT_ID", "\"$artifactId\"")
    buildConfigField("String", "PLUGIN_ID", "\"$pluginId\"")
    buildConfigField("String", "VERSION", "\"$version\"")
}

pluginBundle {
    val publicationUrl: String by project
    val publicationScmUrl: String by project
    val publicationTags: String by project
    website = publicationUrl
    vcsUrl = publicationScmUrl
    tags = publicationTags.split(',')
}

gradlePlugin {
    val resources by plugins.creating {
        val publicationDisplayNamePlugin: String by project
        val publicationDescriptionPlugin: String by project
        id = pluginId
        displayName = publicationDisplayNamePlugin
        description = publicationDescriptionPlugin
        implementationClass = "com.goncalossilva.resources.ResourcesPlugin"
    }
}

signing {
    // Use `signingKey` and `signingPassword` properties to sign artifacts, if provided.
    // Otherwise, default to `signing.keyId`, `signing.password` and `signing.secretKeyRingFile`.
    val signingKey: String? by project
    val signingPassword: String? by project
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    sign(publishing.publications)
}

tasks.named("publish") {
    dependsOn("publishPlugins")
}
