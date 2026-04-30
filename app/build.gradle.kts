import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kspPlugin)
}

val localProps = Properties().also { props ->
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { props.load(it) }
}

android {
    namespace = "com.makhabatusen.access_lab_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.makhabatusen.access_lab_app"
        minSdk = 25
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "YOUTUBE_API_KEY",
            "\"${localProps.getProperty("YOUTUBE_API_KEY", "")}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE*",
                "META-INF/NOTICE*",
                "META-INF/*.kotlin_module",
                "META-INF/ASL2.0",
                "META-INF/LGPL2.1",
                "META-INF/maven/**",
                "META-INF/proguard/**",
                "META-INF/versions/**",
                "META-INF/INDEX.LIST"
            )
        }
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = true
        lintConfig = file("valid-accessibility-lint.xml")
        enable += listOf(
            "ContentDescription", "LabelFor", "ClickableViewAccessibility",
            "KeyboardInaccessibleWidget", "DuplicateIncludedIds", "DuplicateIds",
            "TextFields", "RtlEnabled", "RtlHardcoded", "RtlSymmetry", "RtlCompat",
            "HardcodedText", "ObsoleteLayoutParam", "NestedWeights", "ScrollViewSize",
            "DisableBaselineAlignment", "DrawAllocation", "ExportedContentProvider",
            "ExportedReceiver", "ExportedService", "GrantAllUris", "WorldReadableFiles",
            "WorldWritableFiles", "HandlerLeak", "Wakelock", "ViewHolder"
        )
        disable += listOf("MissingTranslation", "ExtraTranslation", "ImpliedQuantity", "PluralsCandidate")
        htmlReport = true
        xmlReport = true
        textReport = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.coil.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)

    implementation(libs.google.api.services.youtube) {
        exclude(group = "com.google.guava", module = "guava-jdk5")
    }
    implementation(libs.google.api.client.android) {
        exclude(group = "com.google.guava", module = "guava-jdk5")
    }

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    testImplementation(libs.junit)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.accessibility)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.compose.ui.test.accessibility)
}