plugins {
    id("com.android.library")
}

android {
    namespace = "com.android.apksigner"
    compileSdk = 34

    defaultConfig {
        minSdk = 16

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        multiDexEnabled = false
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation ("org.conscrypt:conscrypt-android:2.5.2")
    implementation ("org.bouncycastle:bcpkix-jdk15to18:1.68")
    implementation ("org.bouncycastle:bcprov-jdk15to18:1.68")
    implementation(project(":apksig"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}