# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-obfuscationdictionary ./proguard-dict.txt
-classobfuscationdictionary ./proguard-dict.txt
-packageobfuscationdictionary ./proguard-dict.txt

#retrofit2  混淆
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
# Gson
-keep class com.google.gson.stream.** { *; }
-keepattributes EnclosingMethod

-keep class * implements androidx.viewbinding.ViewBinding {*;}

-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
public static ** bind(***);
public static ** inflate(...);
}

-keep,allowobfuscation,allowshrinking class com.glittering.youxi.ui.activity.BaseActivity
-keep class com.glittering.youxi.data.** { *; }

-dontwarn org.slf4j.impl.StaticLoggerBinder