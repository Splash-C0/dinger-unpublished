-dontobfuscate
-dontpreverify
-optimizations !code/allocation/variable
-keep class sun.misc.Unsafe { *; }
-keepclasseswithmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-dontwarn okhttp3.**
-dontnote okhttp3.**
-dontwarn okio.**
-dontnote okio.**
-dontnote io.reactivex.**
-dontnote com.google.**
-dontnote com.facebook.**
-dontnote com.airbnb.**
-dontnote com.android.**
-dontnote android.**
-dontnote org.apache.**
-dontnote org.reactivestreams.**
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-keepattributes Signature
-keepattributes Exceptions
-dontwarn sun.misc.**
-dontwarn org.junit.**
-dontwarn org.hamcrest.**
-dontwarn com.squareup.javawriter.**
-dontwarn android.test.**
-dontwarn kotlin.jvm.internal.Reflection
-dontwarn com.google.errorprone.annotations.*
-dontwarn com.squareup.okhttp.**
-keep class androidx.appcompat.widget.SearchView { *; }
-dontwarn android.arch.util.paging.CountedDataSource
-dontwarn androidx.room.paging.LimitOffsetDataSource
-dontwarn io.reactivex.internal.**
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn javax.annotation.**
