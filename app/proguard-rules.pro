# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html
#
# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable
#
# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

############

# Kotlin 관련 클래스 보존
-keep class kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Retrofit2 - Type parameters are used by Retrofit for service method interfaces
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Retrofit과 관련된 클래스들을 난독화하지 않도록 설정
-keep class retrofit2.** { *; }
-keep class retrofit2.converter.gson.** { *; }
-keepattributes Signature

# Gson과 관련된 클래스들을 난독화하지 않도록 설정
-keep class com.google.gson.** { *; }

# 데이터 클래스 난독화하지 않도록 설정
-keep class com.nbcfinalteam2.ddaraogae.data.dto.** { *; }

# 데이터 클래스와 제네릭 타입 관련 설정
-keepclassmembers,allowshrinking,allowobfuscation class * {
    @retrofit2.http.* <methods>;
}

# 모든 제네릭 타입의 정보를 보존
-keepattributes Signature

# Google 로그인과 관련된 부분 난독화하지 않도록 설정
-keep class * extends com.google.gson.TypeAdapter
-keep class com.google.googlesignin.** { *; }
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.common.** { *; }




