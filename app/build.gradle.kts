plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kover)
    id("com.google.gms.google-services")
}

apply(from = "../app-version.gradle")

android {
    namespace = "com.akundu.kkplayer"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.akundu.kkplayer"
        minSdk = 33
        targetSdk = 37
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
        getByName("debug") {
            enableAndroidTestCoverage = true
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
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
    implementation(libs.androidx.media)

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
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    // Unit Test Dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.androidx.work.testing)

    // Instrumentation Test Dependencies
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.intents)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "com.akundu.kkplayer.BuildConfig",
                    "*.R",
                    "*.R\$*",
                    "*_Impl",
                    "*ComposableSingletons*",
                )
                packages("com.akundu.kkplayer.ui.theme")
                annotatedBy("androidx.compose.ui.tooling.preview.Preview")
            }
        }
    }
}

/**
 * `./gradlew unitTestSuite` runs every local unit test in one pass through
 * com.akundu.kkplayer.UnitTestSuite.
 *
 * A plain `./gradlew testDebugUnitTest` leaves the suite class out, so the same tests are not
 * executed twice - once directly and once again through the suite.
 */
val isUnitTestSuiteRun = gradle.startParameter.taskNames.any { it.substringAfterLast(':') == "unitTestSuite" }

tasks.withType<Test>().configureEach {
    if (isUnitTestSuiteRun) {
        filter.setIncludePatterns("com.akundu.kkplayer.UnitTestSuite")
        // A suite builds every runner it lists up front, which does not fit in the 512m the
        // Android plugin gives a unit test worker by default.
        maxHeapSize = "2g"
    } else {
        // Excluded at class level: filter.excludeTestsMatching would only match the leaf tests, and
        // the suite would still re-run every class it lists.
        exclude("**/UnitTestSuite.class")
    }
}

tasks.register("unitTestSuite") {
    group = "verification"
    description = "Runs all local unit tests through com.akundu.kkplayer.UnitTestSuite"
    dependsOn("testDebugUnitTest")
}
