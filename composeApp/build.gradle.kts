import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material) // Material 2 para compatibilidad
                implementation(compose.components.resources)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.itext7.core)


            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation("androidx.compose.material3:material3:1.3.0")
                implementation("androidx.compose.material:material-icons-extended:1.3.0")
                implementation("io.coil-kt:coil-compose:2.4.0") // Cargar imagen del logo en Android
                implementation("androidx.navigation:navigation-compose:2.5.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
                implementation("com.google.code.gson:gson:2.8.9")
                implementation("com.itextpdf:itext7-core:7.2.2")
                implementation("com.itextpdf:kernel:7.2.2")
                implementation ("com.sun.mail:android-activation:1.6.2")

                implementation ("com.sun.mail:android-mail:1.6.2")

            }
        }

        val iosMain by creating {
            dependencies {
                // Agregar dependencias específicas de iOS si las necesitas
            }
        }

        val iosTest by creating {
            dependencies {
                // Dependencias para pruebas en iOS
            }
        }
    }
}

android {
    namespace = "pharmapro.carlosnava"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "pharmapro.carlosnava"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }

    dependencies {
        debugImplementation(compose.uiTooling)
    }
}
dependencies {
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.benchmark.macro)
}



