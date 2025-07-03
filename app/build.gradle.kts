plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
}

apply(from = "../app-version.gradle")

android {
    namespace = "com.akundu.kkplayer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.akundu.kkplayer"
        minSdk = 23
        targetSdk = 35

        versionCode = project.ext["major"].toString().toInt() * 100 + project.ext["minor"].toString().toInt() * 10 + project.ext["build"].toString().toInt()
        versionName = "${project.ext["major"]}.${project.ext["minor"]}.${project.ext["build"]}"

        setProperty("archivesBaseName", "$rootProject.name-v$versionName")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
        androidResources {
            localeFilters.addAll(listOf("en", "en-rGB", "en-rIN"))
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }

    kotlinOptions {
        jvmTarget = "18"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    packaging {
        resources.excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
    }
}

// ⚠️ Optional – disabled by default to avoid test issues
// tasks.named("preBuild") {
//     dependsOn("ktlintFormat")
// }

ktlint {
    android.set(true)
    ignoreFailures.set(true)
    disabledRules.set(
        listOf(
            "max-line-length",
            "parameter-list-wrapping",
            "no-multi-spaces",
            "no-consecutive-blank-lines",
            "no-blank-line-before-rbrace",
            "final-newline",
        ),
    )
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.SARIF)
    }
}

dependencies {
    // Android Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.splashscreen)

    // Compose UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.animation.graphics)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)
    implementation(libs.picasso)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Accompanist Pager
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    // Ffmpeg
    implementation(libs.ffmpegmediametadataretriever.core)
    implementation(libs.ffmpegmediametadataretriever.native)

    // 3rd party dependencies
    implementation(libs.toasty)

    // Test Dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.mockito.kotlin.kt1.x)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Jitpack.IO
    implementation("com.github.arnab-kundu:Storage:1.0.5")
    implementation("com.github.arnab-kundu:ESCPOS-ThermalPrinter-Android:v3.4.0")
}
