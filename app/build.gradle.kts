plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.daawahtv"
    compileSdk = 36
    buildToolsVersion = "29.0.3"
    signingConfigs {
        create("release") {
            storeFile = file("dummy.jks")
            storePassword = project.findProperty("KEYSTORE_PASSWORD") as? String ?: "password"
            keyAlias = "daawahtv"
            keyPassword = project.findProperty("KEY_PASSWORD") as? String ?: "password"
        }
    }


    defaultConfig {
        applicationId = "com.example.daawahtv"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release") // تأكد من هذا السطر
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.leanback)
    implementation(libs.glide)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.datasource)
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.okhttp.logging)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.HaarigerHarald:android-youtubeExtractor:master-SNAPSHOT")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    // إذا كنت تستخدم OkHttp interceptor مثلاً:
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")

    testImplementation(libs.junit4)

}