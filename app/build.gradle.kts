plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "edu.guigu.accountbook"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "edu.guigu.accountbook"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true   // ★ 启用 ViewBinding
    }
}

dependencies {
// 基础库
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
// Room 数据库
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
// MVVM
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)
// Fragment
    implementation(libs.androidx.fragment.ktx)
// ViewPager2（Tab 切换）
    implementation(libs.androidx.viewpager2)
// 图表库
// implementation(libs.mpandroidchart)
}