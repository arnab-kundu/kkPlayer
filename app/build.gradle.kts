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

        resourceConfigurations.addAll(listOf("en", "en-rGB", "en-rIN"))
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
        kotlinCompilerVersion = "1.5.10"
    }

    packagingOptions {
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
            "final-newline"
        )
    )
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.SARIF)
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    val retrofitVersion = "2.10.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    val compose_ui_version = "1.5.3"
    implementation("androidx.compose.ui:ui:$compose_ui_version")
    implementation("androidx.compose.material:material:$compose_ui_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_ui_version")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.8")
    implementation("androidx.compose.animation:animation-graphics:1.7.8")

    debugImplementation("androidx.compose.ui:ui-tooling:$compose_ui_version")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.8")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_ui_version")

    val navigationComposeVersion = "2.8.9"
    implementation("androidx.navigation:navigation-compose:$navigationComposeVersion")

    val accompanistPagerVersion = "0.25.1"
    implementation("com.google.accompanist:accompanist-pager:$accompanistPagerVersion")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistPagerVersion")

    implementation("com.github.wseemann:FFmpegMediaMetadataRetriever-core:1.0.19")
    implementation("com.github.wseemann:FFmpegMediaMetadataRetriever-native:1.0.19")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("com.nhaarman:mockito-kotlin-kt1.1:1.5.0")

    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.github.GrenderG:Toasty:1.5.2")

    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    implementation("com.github.arnab-kundu:Storage:1.0.5")

    implementation("com.github.arnab-kundu:ESCPOS-ThermalPrinter-Android:v3.4.0")


}
