// Root Gradle build file: declare plugins for submodules (no apply here)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kspPlugin) apply false
}