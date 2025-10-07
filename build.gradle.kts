// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.google.services)                     // Firebase
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.com.google.devtools.ksp) apply false
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