// Proguard rules (şimdilik boş, release build için gerekli)
# Add project specific ProGuard rules here.

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# SignalR
-keep class com.microsoft.signalr.** { *; }

# Models
-keep class com.systemmonitor.mobile.models.** { *; }
