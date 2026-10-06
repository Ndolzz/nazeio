plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.nazeio.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nazeio.app"
        minSdk = 30
        targetSdk = 35
        versionCode = 6
        versionName = "0.6.0"

        // Spesifikasi induk: target HP ARMv7 32 bit.
        ndk {
            abiFilters += listOf("armeabi-v7a")
        }

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.09.02")
    implementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.2")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Room untuk penyimpanan alias sesuai spesifikasi 02
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Penyimpanan kunci API terenkripsi sesuai spesifikasi 03
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Pengenal suara on-device Bahasa Indonesia (cadangan bila layanan Google tiada)
    implementation("com.alphacephei:vosk-android:0.3.47")

    testImplementation("junit:junit:4.13.2")
}
