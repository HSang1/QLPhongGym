plugins {
    id("com.google.gms.google-services")
    id("com.android.application")
}

android {
    namespace = "com.example.qlphonggym"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.qlphonggym"
        minSdk = 24
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

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity:1.9.3")
    implementation("com.google.zxing:core:3.3.3") //Thêm thư viện zxing
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

    implementation("com.google.firebase:firebase-analytics")


    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore:25.1.1")
    implementation("com.google.firebase:firebase-storage:21.0.1")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation(fileTree(mapOf(
        "dir" to "C:\\Users\\huynh\\Desktop\\ZaloPayLib",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))

    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.7.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")

    // All:
    implementation ("com.cloudinary:cloudinary-android:3.0.2")

// Download + Preprocess:
    implementation ("com.cloudinary:cloudinary-android-download:3.0.2")
    implementation ("com.cloudinary:cloudinary-android-preprocess:3.0.2")


    implementation("commons-codec:commons-codec:1.14")




    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


}