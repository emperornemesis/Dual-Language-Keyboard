# Keep InputMethodService subclass
-keep class com.example.afrikaanskeyboard.AfrikaansEnglishIME { *; }
-keep class com.example.afrikaanskeyboard.SettingsActivity { *; }

# Keep Preference classes used by settings fragment
-keepclassmembers class * extends androidx.preference.Preference {
    <init>(android.content.Context, android.util.AttributeSet);
}

# Kotlin
-keepattributes *Annotation*
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
