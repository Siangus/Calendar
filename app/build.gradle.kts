plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.calendar"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.calendar"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += setOf("arm64-v8a") // 只打包 arm64-v8a
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("C:/Users/26877/Desktop/Calendar/your_keystore.jks")
            storePassword = "siangus"
            keyAlias = "siangus"
            keyPassword = "siangus"
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            ndk {
                abiFilters += setOf("arm64-v8a") // release只包含 arm64-v8a
            }
        }
        getByName("debug") {
            isMinifyEnabled = false
            // debug保留所有常见架构，方便模拟器和真机调试
            ndk {
                abiFilters += setOf("x86_64", "arm64-v8a", "armeabi-v7a", "x86")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom)) // Compose BOM 版本管理

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.activity:activity-compose:1.7.2")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling-preview")

    implementation("androidx.navigation:navigation-compose:2.6.0")
    implementation("com.google.android.material:material:1.9.0") {
        exclude(group = "com.android.support")
    }

    implementation("com.github.prolificinteractive:material-calendarview:2.0.1") {
        exclude(group = "com.android.support")
    }

    implementation("cn.6tail:lunar:1.3.0")

    implementation("com.github.yalantis:ucrop:2.2.8") {
        exclude(group = "com.android.support")
    }
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}