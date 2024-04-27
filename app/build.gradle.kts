plugins {
    id("kotlin.concurrent.kotlin-application-conventions")
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.kotlinx.coroutines)
    implementation(libs.arrow.kt.core)
    implementation(libs.arrow.kt.coroutines)
    implementation(libs.arrow.kt.stm)
    implementation(libs.arrow.kt.optics)
    ksp(libs.arrow.kt.optics.ksp.plugin)
    implementation(project(":kotlin-arrow"))
    implementation(project(":kotlin-coroutines"))
}

application {
    mainClass.set("com.leysoft.concurrent.app.AppKt")
}
