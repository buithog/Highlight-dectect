plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.music_highlight_detect"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.music_highlight_detect"
        minSdk = 34
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    androidResources {
        noCompress += "tflite"
    }

}

dependencies {
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("org.tensorflow:tensorflow-lite:2.13.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(files("C:\\Users\\fpt\\AndroidStudioProjects\\Music_highlight_detect\\app\\libs\\jlibrosa-1.1.8.jar"))
}