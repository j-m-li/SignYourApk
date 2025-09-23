plugins {
    id("com.android.application")
}

android {
    namespace = "com.cod5.signyourapk"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cod5.signyourapk"
        minSdk = 16
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    experimentalProperties["android.ndk.suppressMinSdkVersionError"]=21


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("commons-cli:commons-cli:1.4")
    implementation("com.madgag.spongycastle:bcpkix-jdk15on:1.56.0.0")
    implementation ("org.conscrypt:conscrypt-android:2.5.3")
    implementation ("org.bouncycastle:bcpkix-jdk15to18:1.68")
    implementation ("org.bouncycastle:bcprov-jdk15to18:1.68")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(project(":apksigner"))
    implementation(project(":apksig"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}