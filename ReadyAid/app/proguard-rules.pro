# Add project specific ProGuard rules here.
# ReadyAid: keep Room entities and Hilt components
-keep class com.readyaid.data.** { *; }
-keep class com.readyaid.di.** { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @dagger.hilt.android.* <methods>;
}
-dontwarn javax.annotation.**
