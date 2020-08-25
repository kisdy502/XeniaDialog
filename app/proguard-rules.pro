-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose

#保护Android四大组件等
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.android.content.BroadcastReceiver
-keep public class * extends android.android.content.ContentProvider

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

#-dontwarn okhttp3.**
#-keep class okhttp3.**{*;}

-dontwarn okio.**
-keep class okio.**{*;}

-dontwarn retrofit2.**
-keep class retrofit2.**{*;}

-dontwarn rx.**
-keep class rx.**{*;}

#-dontwarn com.facebook.**
#-keep class com.facebook.**{*;}

-dontwarn com.uber.**
-keep class com.uber.**{*;}

# ijkplayer
-keep class tv.danmaku.ijk.media.player.** {*;}
-keep class tv.danmaku.ijk.media.player.IjkMediaPlayer {*;}
-keep class tv.danmaku.ijk.media.player.ffmpeg.FFmpegApi {*;}
