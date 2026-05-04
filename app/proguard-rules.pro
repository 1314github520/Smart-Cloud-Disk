# 保护实体类（数据库字段不能乱变）
-keep class com.example.clouddisk.User { *; }
-keep class com.example.clouddisk.CloudFile { *; }
-keep class com.example.clouddisk.TransferTask { *; }
-keep class com.example.clouddisk.FileIndex { *; }

# 保护 Room 核心代码
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public <init>(...);
}

# 保护 Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.** { *; }

# 保护 OkHttp 和 Okio
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
