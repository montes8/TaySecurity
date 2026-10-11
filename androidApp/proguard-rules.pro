# Proguard rules for TaySecurity Release

# SQLite & SQLDelight
-dontwarn org.sqlite.**
-dontwarn java.sql.**

# Keep Gson models
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *; }

# Keep Hilt / Dagger
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keepclassmembers class * {
    @dagger.hilt.** *;
}

# Keep Coroutines
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** { *; }

# Strip Log.d and Log.v in Release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}
