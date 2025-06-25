plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) // Keep this
    alias(libs.plugins.kotlin.compose)
    // id("kotlin-android") // Remove this duplicate
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.stulogandroidapp"
    compileSdk = 35 // Ensure SDK Platform 35 is installed and you intend to use it

    defaultConfig {
        applicationId = "com.example.stulogandroidapp"
        minSdk = 24
        targetSdk = 35 // Ensure this aligns with your testing and feature needs
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        compose = true
        viewBinding = true
    }
}

dependencies {
    // For colour picking - uncomment exclude if it causes duplicate annotation errors
    implementation("com.github.QuadFlask:colorpicker:0.0.15") {
        // exclude(group = "com.intellij", module = "annotations")
    }

    implementation(libs.androidx.core.ktx) // Keep one declaration
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom)) // For Compose testing
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Core Android (Non-Compose UI)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // RecyclerView + ViewPager2
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)

    // Room (Database)
    implementation(libs.androidx.room.runtime)
    // implementation(libs.androidx.room.compiler) // This should be 'kapt' or 'ksp'
    kapt(libs.androidx.room.compiler) // Correct for kapt plugin
    implementation(libs.androidx.room.ktx)

    // Navigation Component
    implementation(libs.androidx.navigation.fragment.ktx) // Keep one declaration
    implementation(libs.androidx.navigation.ui.ktx)      // Use the version from the catalog
    // Remove: implementation(libs.androidx.navigation.ui.ktx.v277)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // For image picking
    implementation(libs.androidx.activity.ktx)

    // Optional: Animations
    implementation(libs.androidx.transition)
}