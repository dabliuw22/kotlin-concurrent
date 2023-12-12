plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "kotlin-concurrent"
include(
    "app",
    "kotlin-arrow",
    "kotlin-coroutines"
)
