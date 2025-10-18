plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.careavatar.userapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.careavatar.userapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val mapsApiKey: String? = project.findProperty("GOOGLE_MAPS_API_KEY") as? String
        if (!mapsApiKey.isNullOrEmpty()) {
            // Use the same name your manifest expects
            resValue("string", "google_api_key", mapsApiKey)
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
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

    ksp("com.google.dagger:hilt-compiler:2.54")
    implementation("com.google.dagger:hilt-android:2.54")

    implementation( project(":core-service"))
    implementation( project(":core-network"))
    implementation( project(":core-utils"))
    implementation( project(":core-model"))
    implementation( project(":core-ui"))
    implementation( project(":login-module"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}