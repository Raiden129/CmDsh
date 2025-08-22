###########################
# Jetpack Compose
###########################
# Keep Compose runtime (reflection needed for Preview/Animation inspector)
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.**

# Keep Material3 & Foundation
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.foundation.** { *; }

# Keep generated @Composable functions metadata
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

###########################
# Kotlin Serialization
###########################
# Keep kotlinx.serialization runtime
-keep class kotlinx.serialization.** { *; }
-keep class kotlinx.serialization.internal.** { *; }
-keepattributes *Annotation*

# Keep @Serializable classes (replace with your model package if you want stricter rules)
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}

###########################
# libVLC
###########################
# Keep all libVLC classes (JNI bridge needs them)
-keep class org.videolan.** { *; }
-dontwarn org.videolan.**

###########################
# AndroidX & Lifecycle
###########################
# ViewModel, LiveData, Lifecycle reflection support
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

###########################
# Navigation Compose (SafeArgs / Reflection)
###########################
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

###########################
# General
###########################
# Keep annotations (needed for Compose + Serialization)
-keepattributes *Annotation*, InnerClasses, Signature
