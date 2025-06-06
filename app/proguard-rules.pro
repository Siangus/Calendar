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
# 保留所有 Activity、Service、BroadcastReceiver 和 ContentProvider，避免运行时找不到类
-keep class * extends android.app.Activity
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider

# 保留 Kotlin 标准库类（防止 Kotlin 相关问题）
-keepclassmembers class kotlin.Metadata { *; }
-keep class kotlin.** { *; }
-dontwarn kotlin.**

# 保留 Compose 相关类，防止 Compose 运行异常
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }

# 保留 UCrop 库相关类，避免裁剪功能异常
-keep class com.yalantis.ucrop.** { *; }

# 保留所有带注解的类和方法（防止注解处理相关的运行时错误）
-keepattributes *Annotation*

# 保留实现 Serializable 接口的类的序列化相关方法
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
