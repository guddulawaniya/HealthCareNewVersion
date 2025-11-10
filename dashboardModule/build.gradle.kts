plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.careavatar.dashboardmodule"
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
    buildFeatures{
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    // For ViewModel & Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")

// For viewModels() delegate in Activity
    implementation("androidx.activity:activity-ktx:1.9.3")

// For viewModels() delegate in Fragment
    implementation("androidx.fragment:fragment-ktx:1.8.5")


    implementation(project(":alzimer-module"))
    implementation(project(":core-service"))
    implementation(project(":core-network"))
    implementation(project(":core-ui"))
    implementation(project(":core-model"))
    implementation(project(":core-utils"))
    implementation(project(":community-module"))
    implementation(project(":service-module"))
    implementation(project(":Upscale-module"))
    implementation(project(":core-navigation"))
    implementation(project(":Medical-reminder"))
    implementation(project(":dietition-module"))

    ksp("com.google.dagger:hilt-compiler:2.54")
    implementation("com.google.dagger:hilt-android:2.54")
    implementation ("com.hbb20:ccp:2.7.0")
    implementation ("com.googlecode.libphonenumber:libphonenumber:8.12.47")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("io.github.chaosleung:pinview:1.4.4")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.tbuonomo:dotsindicator:4.3")
    //firebase
    implementation("com.google.firebase:firebase-messaging:23.1.0")
    implementation("com.google.firebase:firebase-analytics:21.0.0")
    implementation("com.google.firebase:firebase-core:9.6.1")
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}