plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myapplicationmeituan"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.myapplicationmeituan"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // RecyclerView 所有列表都要用（店铺、菜品、购物车）
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    // ViewModel + LiveData 存购物车（核心）
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.4")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.4")
    // 图片加载 Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.activity.ktx)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    // Retrofit 后面对接后端网络
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}