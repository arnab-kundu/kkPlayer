plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

apply(from = "../app-version.gradle")

android {
    namespace = "com.akundu.kkplayer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.akundu.kkplayer"
        minSdk = 23
        targetSdk = 36
        versionCode =
            project.ext["major"].toString().toInt() * 100 + project.ext["minor"].toString().toInt() * 10 +
            project.ext["build"].toString().toInt()
        versionName = "${project.ext["major"]}.${project.ext["minor"]}.${project.ext["build"]}"

        // Rename APK programmatically
        applicationVariants.all {
            outputs.all {
                (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                    "${rootProject.name}-v$versionName.apk"
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
        androidResources {
            noCompress += "tflite"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
}

// ⚠️ Optional – disabled by default to avoid test issues
tasks.named("preBuild") {
    dependsOn("ktlintFormat")
}

ktlint {
    android.set(true)
    ignoreFailures.set(true)
    // disabledRules.set(
    //     listOf(
    //         "max-line-length",
    //         "parameter-list-wrapping",
    //         "no-multi-spaces",
    //         "no-consecutive-blank-lines",
    //         "no-blank-line-before-rbrace",
    //         "final-newline",
    //         "no-newline-after-opening-parenthesis"
    //     ),
    // )
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
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.google.android.material)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.datastore.preferences)

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
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.converter.gson)
    implementation(libs.squareup.logging.interceptor)
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.picasso)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Accompanist Pager
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    // 3rd party dependencies
    implementation(libs.toasty)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.14.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Test Dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.mockito.kotlin.kt1.x)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}
