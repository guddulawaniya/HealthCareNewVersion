plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.asyscraft.medical_reminder"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    val cameraxVersion = "1.3.4"
    implementation ("androidx.camera:camera-core:$cameraxVersion")
    implementation ("androidx.camera:camera-camera2:$cameraxVersion")
    implementation ("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation ("androidx.camera:camera-view:$cameraxVersion")
// Required for ListenableFuture (Fixes your error)
    implementation("com.google.guava:guava:31.1-android")

    implementation("com.google.android.material:material:1.13.0-alpha07")
    implementation("com.google.android.material:material:1.12.0")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.paging.runtime.ktx)

    implementation("com.github.antonKozyriatskyi:CircularProgressIndicator:1.3.0")
    // For ViewModel & Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")

// For viewModels() delegate in Activity
    implementation("androidx.activity:activity-ktx:1.9.3")

// For viewModels() delegate in Fragment
    implementation("androidx.fragment:fragment-ktx:1.8.5")

    //google map
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    // calender
    implementation ("com.prolificinteractive:material-calendarview:1.4.3")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Hilt
    ksp("com.google.dagger:hilt-compiler:2.54")
    implementation("com.google.dagger:hilt-android:2.54")
    implementation ("com.hbb20:ccp:2.7.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation(project(":core-service"))
    implementation(project(":core-network"))
    implementation(project(":core-ui"))
    implementation(project(":core-model"))
    implementation(project(":core-utils"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}