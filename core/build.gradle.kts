plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
}

android {
    namespace = libs.versions.applicationId.get()
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.databinding.common)
    api(project(":annotation"))
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.iwdael.permissionsdispatcher"
            artifactId = "dispatcher"
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}