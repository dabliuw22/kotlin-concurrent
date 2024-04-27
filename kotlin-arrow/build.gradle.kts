plugins {
    id("kotlin.concurrent.kotlin-library-conventions")
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.arrow.kt.core)
    implementation(libs.arrow.kt.coroutines)
    implementation(libs.arrow.kt.stm)
    implementation(libs.arrow.kt.optics)
    ksp(libs.arrow.kt.optics.ksp.plugin)
}
