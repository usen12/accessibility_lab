# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep accessibility-related classes and methods
-keep class com.makhabatusen.access_lab_app.oldui.** { *; }
-keep class com.makhabatusen.access_lab_app.util.AccessibilityUtils { *; }
-keep class com.makhabatusen.access_lab_app.util.UnifiedButton { *; }
-keep class com.makhabatusen.access_lab_app.util.UnifiedTopBar { *; }

# Keep Compose UI components
-keep class androidx.compose.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.runtime.** { *; }

# Keep Room database classes
-keep class com.makhabatusen.access_lab_app.notes.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep YouTube API classes
-keep class com.google.api.services.youtube.** { *; }
-keep class com.google.api.client.** { *; }

# Keep Media3 classes
-keep class androidx.media3.** { *; }

# Keep Retrofit and OkHttp
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep interface retrofit2.** { *; }

# Keep JSON serialization
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
