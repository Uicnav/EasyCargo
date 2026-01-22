import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    val iosArm64Target = iosArm64()
    val iosSimulatorArm64Target = iosSimulatorArm64()

    listOf(iosArm64Target, iosSimulatorArm64Target).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                implementation(libs.lifecycle.viewmodel.compose)
                implementation(libs.navigation.compose)

                implementation(libs.kotlinx.serialization.json)

                implementation(libs.room.runtime)
                implementation(libs.sqlite.bundled)

                implementation(libs.kotlinx.datetime)

                api(libs.datastore.preferences)
                api(libs.datastore)

                implementation(compose.materialIconsExtended)

                implementation(libs.alarmee)

            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.review)
                // Potrebbe essere necessaria anche la versione KTX
                implementation(libs.review.ktx)
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.navigation.runtime.ktx)
                implementation(libs.androidx.fragment.ktx)
                implementation(libs.kotlinx.coroutines.play.services)
                //implementation(libs.play.services.ads)
            }
        }

        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

android {
    namespace = "com.vantechinformatics.easycargo"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.vantechinformatics.easycargo"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 160
        versionName = "1.6.0"
    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        debug {
            ndk {
                abiFilters.clear()
                abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
                debugSymbolLevel = "SYMBOL_TABLE"
            }
            packaging {
                jniLibs {
                    useLegacyPackaging = false
                }
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            ndk {
                abiFilters.clear()
                abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
                debugSymbolLevel = "SYMBOL_TABLE"
            }
            packaging {
                jniLibs {
                    useLegacyPackaging = false
                }
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    ksp(libs.room.compiler)
    add("kspAndroid", libs.room.compiler)

    debugImplementation(libs.compose.ui.tooling)

    coreLibraryDesugaring(libs.desugar.jdk.libs)
}


