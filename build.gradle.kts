plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.firebaseplugin)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
    alias(libs.plugins.googleplugin)
}

android {
    namespace = "com.phonecontactscall.contectapp.phonedialerr"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.phonecontactscall.contectapp.phonedialerr"
        minSdk = 26
        targetSdk = 34
        versionCode = 14
        versionName = "1.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildFeatures {
        viewBinding = true
    }
    bundle {
        language {
            enableSplit = false
        }
    }
    android {
        lint {
            baseline = file("lint-baseline.xml")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation(libs.androidx.multidex)
    implementation(libs.eventbus)
    implementation (libs.slidetoact)
//    implementation(libs.indicator.fast.scroll)
    implementation(libs.lottie)
    implementation (libs.roundedimageview)


    implementation (libs.google.gson)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.converter.gson)
    // room database
    implementation (libs.room.runtime)
    kapt (libs.compiler)
    implementation(libs.room.ktx)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.runtime)

    implementation(libs.glide)
    implementation(libs.app.update)
    implementation(libs.play.services.ads)
    // firebase
    // Firebase implementation
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.config)
    implementation(libs.firebase.messaging)

    implementation (libs.review)
    implementation ("com.microsoft.clarity:clarity:2.3.0")

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    implementation("com.google.firebase:firebase-perf")
    implementation("com.google.ads.mediation:facebook:6.18.0.0")
    implementation ("com.google.android.ump:user-messaging-platform:2.1.0")


}