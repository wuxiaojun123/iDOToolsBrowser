# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/wuxiaojun/adt-bundle-linux-x86_64-20140702/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 2
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}
###################AutoUpdate#######################
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes *Annotation*

-keep class com.dot.autoupdater.AutoUpdater { *; }
-keep class com.dot.autoupdater.checker.UpdateResultCallback { *; }
-keep class com.dot.autoupdater.version.VersionProfile { *; }

-keep class com.dot.autoupdater.AutoUpdater$* {
        <fields>;
        <methods>;
}
-keepclassmembers class com.dot.autoupdater.AutoUpdater$* { *; }

-keep class **.R$* {
        public static <fields>;
}
-keepclassmembers class **.R$* { *; }

###################AutoUpdate#######################
#####################阿里百川的意见反馈 start###########################
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.alipay.** {*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.alipay.**
-keep class com.ut.** {*;}
-dontwarn com.ut.**
-keep class com.ta.** {*;}
-dontwarn com.ta.**
-keep class com.alibaba.sdk.android.feedback.** {*;}
#####################阿里百川的意见反馈 end###########################