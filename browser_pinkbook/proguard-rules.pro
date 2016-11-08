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
-optimizationpasses 5
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

-keepclasseswithmembernames class * {#保持native方法不被混淆
    native <methods>;
}

-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}

-keep public class com.idotools.browser.pink.book.R$*{
    public static final int *;
}

#####################自定义view还有和js交互的类 不混淆 ############################
-keep class com.idotools.browser.pink.book.view.** { *; }
-keep class com.idotools.browser.pink.book.manager.webview.BrowserJsInterface { *; }
#####################自定义view 不混淆 ############################

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


######################小强统计 start#######################
# For Umeng SDK
-dontwarn com.umeng.**
-keep class com.umeng.** { *; }

-dontwarn okio.**
-dontwarn org.apache.**
-dontwarn ch.qos.**
#
-dontwarn javax.mail.**
-dontwarn javax.naming.Context
-dontwarn javax.naming.InitialContext
-dontwarn javax.mail.**, javax.naming.Context, javax.naming.InitialContext

######################################################
# <<< proguard rules for analytics one >>>
######################################################
-keep class com.dot.analyticsone.AnalyticsOne { *; }
-keep class com.dot.analyticsone.CaptureMask { *; }
-keep class com.dot.analyticsone.EventTraits { *; }
-keep class com.dot.analyticsone.integrations.** { *; }


######################################################
# <<< proguard rules for ido analytics >>>
######################################################
-keep class com.dot.analytics.DotAnalytics { *; }
-keep class com.dot.analytics.EventPriority { *; }
-keep class com.dot.analytics.userinfo.Account { *; }
-keep class ch.qos.logback.core.rolling.TBRPPolicy { *; }
-keep class com.dot.analytics.utils.Jni { *; }

-dontwarn ch.qos.**
-dontwarn org.apache.**

-keep class org.slf4j.Logger { *; }
-keep class org.slf4j.LoggerFactory { *; }
-keep class ch.qos.logback.classic.android.LogcatAppender { *; }
-keep class ch.qos.logback.classic.encoder.PatternLayoutEncoder { *; }
-keep class ch.qos.logback.core.rolling.RollingFileAppender { *; }
-keep class ch.qos.logback.core.rolling.RolloverFailure  { *; }
-keep class ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP  { *; }
-keep class ch.qos.logback.core.rolling.TimeBasedRollingPolicy  { *; }


######################################################
# <<< proguard rules for google analytics >>>
######################################################
-dontwarn com.google.android.gms.**

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}


######################################################
# <<< proguard rules for umeng analytics >>>
######################################################
-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}
#######################小强统计 end####################


#################### butterknife start################
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
##################### butterknife end################

#####################glide start###################
#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
####################glide end#####################

####################个推　start###################
-dontwarn com.igexin.**
-keep class com.igexin.**{*;}
####################个推 end#####################