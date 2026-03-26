package com.example.afrikaanskeyboard

/**
 * Defines all key layouts used by the IME.
 * Each layout is a list of rows; each row is a list of KeyDef objects.
 */
object KeyboardData {

    /** A single key definition. */
    data class KeyDef(
        val label: String,
        val code: Int = label.first().code,
        val type: KeyType = KeyType.CHAR,
        val widthWeight: Float = 1f
    )

    enum class KeyType {
        CHAR,       // Regular character key
        BACKSPACE,  // Delete key
        RETURN,     // Enter / newline
        SHIFT,      // Caps / shift toggle
        SPACE,      // Space bar
        MODE,       // Switch between QWERTY / symbols
        LANGUAGE,   // Toggle language
        DISMISS     // Dismiss / minimize keyboard
    }

    val NUMERIC_ROW: List<KeyDef> = listOf(
        KeyDef("1"), KeyDef("2"), KeyDef("3"), KeyDef("4"), KeyDef("5"),
        KeyDef("6"), KeyDef("7"), KeyDef("8"), KeyDef("9"), KeyDef("0")
    )

    val QWERTY: List<List<KeyDef>> = listOf(
        listOf(
            KeyDef("q"), KeyDef("w"), KeyDef("e"), KeyDef("r"), KeyDef("t"),
            KeyDef("y"), KeyDef("u"), KeyDef("i"), KeyDef("o"), KeyDef("p")
        ),
        listOf(
            KeyDef("a"), KeyDef("s"), KeyDef("d"), KeyDef("f"), KeyDef("g"),
            KeyDef("h"), KeyDef("j"), KeyDef("k"), KeyDef("l")
        ),
        listOf(
            KeyDef("⇧", code = -1, type = KeyType.SHIFT, widthWeight = 1.5f),
            KeyDef("z"), KeyDef("x"), KeyDef("c"), KeyDef("v"),
            KeyDef("b"), KeyDef("n"), KeyDef("m"),
            KeyDef("⌫", code = -2, type = KeyType.BACKSPACE, widthWeight = 1.5f)
        )
    )

    val BOTTOM_ROW: List<KeyDef> = listOf(
        KeyDef("!#1", code = -3, type = KeyType.MODE, widthWeight = 1.5f),
        KeyDef("LANG", code = -5, type = KeyType.LANGUAGE, widthWeight = 1.2f),
        KeyDef("space", code = 32, type = KeyType.SPACE, widthWeight = 5f),
        KeyDef("↩", code = 10, type = KeyType.RETURN, widthWeight = 1.5f)
    )

    val SYMBOLS_PAGE_1: List<List<KeyDef>> = listOf(
        listOf(
            KeyDef("!"), KeyDef("@"), KeyDef("#"), KeyDef("$"), KeyDef("%"),
            KeyDef("^"), KeyDef("&"), KeyDef("*"), KeyDef("("), KeyDef(")")
        ),
        listOf(
            KeyDef("-"), KeyDef("_"), KeyDef("="), KeyDef("+"), KeyDef("["),
            KeyDef("]"), KeyDef("{"), KeyDef("}"), KeyDef("|")
        ),
        listOf(
            KeyDef("pg2", code = -4, type = KeyType.MODE, widthWeight = 1.5f),
            KeyDef(";"), KeyDef(":"), KeyDef("'"), KeyDef("\""),
            KeyDef(","), KeyDef("."), KeyDef("?"), KeyDef("/"),
            KeyDef("⌫", code = -2, type = KeyType.BACKSPACE, widthWeight = 1.5f)
        )
    )

    val SYMBOLS_PAGE_2: List<List<KeyDef>> = listOf(
        listOf(
            KeyDef("à"), KeyDef("á"), KeyDef("â"), KeyDef("ä"), KeyDef("ã"),
            KeyDef("å"), KeyDef("æ"), KeyDef("ç"), KeyDef("è"), KeyDef("é")
        ),
        listOf(
            KeyDef("ê"), KeyDef("ë"), KeyDef("ì"), KeyDef("í"), KeyDef("î"),
            KeyDef("ï"), KeyDef("ò"), KeyDef("ó"), KeyDef("ô"), KeyDef("ö")
        ),
        listOf(
            KeyDef("pg1", code = -4, type = KeyType.MODE, widthWeight = 1.5f),
            KeyDef("õ"), KeyDef("ù"), KeyDef("ú"), KeyDef("û"), KeyDef("ü"),
            KeyDef("ý"), KeyDef("ñ"), KeyDef("ß"),
            KeyDef("⌫", code = -2, type = KeyType.BACKSPACE, widthWeight = 1.5f)
        )
    )

    val SYMBOLS_BOTTOM_ROW: List<KeyDef> = listOf(
        KeyDef("ABC", code = -3, type = KeyType.MODE, widthWeight = 1.5f),
        KeyDef("LANG", code = -5, type = KeyType.LANGUAGE, widthWeight = 1.2f),
        KeyDef("space", code = 32, type = KeyType.SPACE, widthWeight = 5f),
        KeyDef("↩", code = 10, type = KeyType.RETURN, widthWeight = 1.5f)
    )
}
