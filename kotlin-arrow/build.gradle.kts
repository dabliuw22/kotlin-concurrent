plugins {
    id("kotlin.concurrent.kotlin-library-conventions")
    alias(libs.plugins.kotlinter)
}

dependencies {
    implementation(libs.arrow.kt.core)
    implementation(libs.arrow.kt.coroutines)
    implementation(libs.arrow.kt.stm)
}
