plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("maven-publish")
}

dependencies {
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.databinding.common)
    implementation(libs.kotlin.symbol.processor)
    implementation(libs.symbol.processing.api)
    implementation(project(":annotation"))
}


val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
    from("README.md") {
        into("META-INF")
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.iwdael.dbroom"
            artifactId = "compiler"
            from(components["java"])
            artifact(sourcesJar)
        }
    }
}

