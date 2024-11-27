plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.ksp)
}
ksp {
    arg("room.incremental", "true")
    arg("logLevel", "DEBUG")
}

android {
    namespace = "${libs.versions.applicationId.get()}.example"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "${libs.versions.applicationId.get()}.example"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.databinding.common)
    implementation(libs.kotlin.symbol.processor)
    implementation(project(  ":core"))
    ksp(libs.androidx.room.compiler)
    ksp(project(":compiler"))
}