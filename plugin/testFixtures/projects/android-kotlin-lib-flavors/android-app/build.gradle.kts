plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.wasm2class.android.kotlin.lib.app"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.wasm2class.android.kotlin.lib.app"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            applicationIdSuffix = ".release"
            isMinifyEnabled = true
            isShrinkResources = false
            signingConfig = signingConfigs["debug"]
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        create("staging") {
            initWith(get("release"))
            applicationIdSuffix = ".staging"
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("demo") {
            applicationIdSuffix = ".demo"
            dimension = "version"
        }
        create("full") {
            applicationIdSuffix = ".full"
            dimension = "version"
        }
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        checkOnly.add("NewApi")
        checkDependencies = false
    }
}

dependencies {
    implementation(project(":android-lib1"))
}
