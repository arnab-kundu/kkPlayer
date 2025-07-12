// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val composeUiVersion by extra("1.5.3")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.8.2")   // Ladybug Feature Drop    (8.8.0 - 8.8.2)
        // classpath("com.android.tools.build:gradle:8.5.1") // Koala    (8.5.0 - 8.5.2)
        // classpath("com.android.tools.build:gradle:8.4.2") // JellyFish (8.4.0 - 8.4.2)
        // classpath("com.android.tools.build:gradle:8.3.2") // Iguana   (8.3.0 - 8.3.2)
        // classpath("com.android.tools.build:gradle:8.2.2") // Hedgehog (8.2.0 - 8.2.2)

        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
        classpath("com.google.gms:google-services:4.4.3")         // Firebase
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
    alias(libs.plugins.kotlin.compose) apply false
}
tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

tasks.register("Test")
// TODO (In Windows) Run `gradlew task` comment in Terminal to get all available gradlew methods
// TODO (In Mac) Press [⌘ + ↩] to Run `gradlew task` comment in Terminal to get all available gradlew methods

tasks.register("ktlintCheck") {

}
// TODO to run ktlintCheck from terminal execute "./gradlew ktlintCheck"