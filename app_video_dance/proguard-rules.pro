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
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.annotation.**
-keep public class * extends android.support.v7.**

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

-keep public class com.gp.browser.video.dance.R$*{
    public static final int *;
}

#####################自定义view还有和js交互的类 不混淆 ##############
-keep class com.base.browser.view.** { *; }
-keep class com.base.browser.bean.** { *; }
-keep class com.base.browser.manager.webview.BrowserJsInterface { *; }

-keep class com.gp.browser.video.dance.view.** { *; }
-keep class com.gp.browser.video.dance.bean.** { *; }
#-keep class com.idotools.browser.manager.http.AppHttpClient {*;}
#####################自定义view 不混淆 ############################

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

#####################glide start###################
#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
####################glide end#####################

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

#################### retrofit ###################

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

################### retrofit end ###################

#-keep public class com.gp.android.gms.ads.** {
#   public *;
#}
#
#-keep public class com.gp.ads.** {
#   public *;
#}
#
#-keep class com.firebase.** { *; }

-keepattributes EnclosingMethod
-keepattributes InnerClasses

-dontwarn okio.**

-keep public class org.jsoup.** {
      public *;
}
