# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep Room entities/DAOs (safe default; only matters once isMinifyEnabled = true)
-keep class cocm.glass.note.pr.data.local.entities.** { *; }
-keep class cocm.glass.note.pr.data.local.dao.** { *; }

# Keep domain models (Gson-serialized for backup/restore)
-keep class cocm.glass.note.pr.domain.model.** { *; }

# Gson uses reflection
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
