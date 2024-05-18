plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "kr.ac.yuhan.cs.yuhan19plus"
    compileSdk = 34

    defaultConfig {
        applicationId = "kr.ac.yuhan.cs.yuhan19plus"
        minSdk = 33
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

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("io.github.bootpay:android:+") //최신 버전 추천
    implementation ("io.github.bootpay:android-bio:+") //생체인증 결제 사용시 추가
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // 파이어베이스
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-firestore:24.11.1")
    implementation("com.google.firebase:firebase-storage:20.3.0")

    // 임성준
    implementation("com.github.fornewid:neumorphism:0.3.2") // 뉴모피즘 추가
    implementation("com.google.android.flexbox:flexbox:3.0.0") // flexbox

    // 오자현
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")// QR 스캔 라이브러리
    implementation("com.github.bumptech.glide:glide:4.13.2"); // Glide 라이브러리 추가
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2");
}