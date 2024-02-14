plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.bugsnag.android)
}

android {
    compileSdk = 34

    defaultConfig {
        namespace = "com.zacharee1.systemuituner"
        applicationId = "com.zacharee1.systemuituner"
        minSdk= 23
        targetSdk = 34
        versionCode = 361
        versionName = versionCode.toString()

        base.archivesName.set("SystemUITuner_${versionCode}")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        aidl = true
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }

    androidResources {
        generateLocaleConfig = true
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {
    implementation(fileTree("libs") { include("*.jar") })

    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.annotation)
    implementation(libs.preference.ktx)
    implementation(libs.slidingpanelayout)
    implementation(libs.work.runtime.ktx)

    implementation(libs.material)
    implementation(libs.gson)

    implementation(libs.billing)
    implementation(libs.billing.ktx)

    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    implementation(libs.hiddenapibypass)
    implementation(libs.indicatorFastScroll)
    implementation(libs.libsu.core)
    implementation(libs.recyclerview.animators)
    implementation(libs.markwon.core)
    implementation(libs.markwon.html)
    implementation(libs.taskerpluginlibrary)
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
    implementation(libs.autofittextview)

    implementation(libs.zacharee.material)
    implementation(libs.patreonSupportersRetrieval)
    implementation(libs.colorpicker)
    implementation(libs.seekBarPreference)
    implementation(libs.collapsiblePreferenceCategory)
    implementation(libs.android.expandableTextView)
    implementation(libs.composeIntroSlider)
    implementation(libs.systemUITunerSystemSettings)

    implementation(libs.bugsnag.android)
    implementation(libs.relinker)

    implementation(libs.accompanist.themeadapter.material3)
    implementation(libs.accompanist.drawablepainter)

    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.compose.animation)
    implementation(libs.compose.foundation.layout)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.ktx)
}
