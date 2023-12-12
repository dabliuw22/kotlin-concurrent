plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlinter)
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
}

kotlinter {
    ignoreFailures = false
    reporters = arrayOf("checkstyle", "plain")
}
