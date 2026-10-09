package com.example.voice

import java.util.Locale

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val locale: Locale,
    val wakePhrases: List<String>,
    val byePhrases: List<String>
) {
    ENGLISH(
        code = "en-US",
        displayName = "English",
        nativeName = "English",
        locale = Locale.US,
        wakePhrases = listOf("hey parul", "parul", "ok parul", "wake up"),
        byePhrases = listOf("bye", "bye parul", "goodbye", "goodbye parul", "turn off")
    ),
    HINDI(
        code = "hi-IN",
        displayName = "Hindi",
        nativeName = "हिन्दी",
        locale = Locale("hi", "IN"),
        wakePhrases = listOf("हे पारुल", "सुनो पारुल", "पारुल", "hey parul", "parul"),
        byePhrases = listOf("अलविदा", "बाय", "बाय पारul", "अलविदा पारुल", "बंद करो", "bye")
    ),
    BENGALI(
        code = "bn-IN",
        displayName = "Bengali",
        nativeName = "বাংলা",
        locale = Locale("bn", "IN"),
        wakePhrases = listOf("হে পারুল", "পারুল", "শুনছো পারুল", "hey parul", "parul"),
        byePhrases = listOf("বিদায়", "বাই", "বিদায় পারুল", "বাই পারুল", "বন্ধ করো", "bye")
    )
}
