package com.example.afrikaanskeyboard

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.afrikaanskeyboard.KeyboardData.KeyDef
import com.example.afrikaanskeyboard.KeyboardData.KeyType

/**
 * CustomKeyboardView — a fully programmatic keyboard built from LinearLayouts.
 *
 * No XML inflation needed; each key is a TextView styled and wired at runtime.
 * Supports:
 *   - QWERTY / Symbols Page 1 / Symbols Page 2
 *   - Numeric row toggle
 *   - Caps-lock (single-tap = temporary shift, double-tap = caps-lock)
 *   - Configurable height
 *   - Haptic feedback
 *   - Language indicator on the LANG key
 */
class CustomKeyboardView(
    context: Context,
    private val heightDp: Int,
    private val showNumericRow: Boolean,
    private val hapticEnabled: Boolean,
    private var language: DictionaryManager.Language,
    private val listener: KeyListener
) : LinearLayout(context) {

    interface KeyListener {
        fun onCharKey(char: String)
        fun onBackspace()
        fun onReturn()
        fun onSpace()
        fun onLanguageToggle()
        fun onDismiss()
    }

    // ── Modes: QWERTY, Sym1, Sym2 ─────────────────────────────────────────────
    private enum class Mode { QWERTY, SYMBOLS_1, SYMBOLS_2 }
    private var mode = Mode.QWERTY

    // ── Caps state ────────────────────────────────────────────────────────────
    private var shiftOn = false     // single-tap shift (applies once)
    private var capsLock = false    // double-tap shift (permanent)
    private var lastShiftTap = 0L

    // ── Key button references we need to update dynamically ───────────────────
    private val keyViews = mutableListOf<TextView>()
    private var langKeyView: TextView? = null
    private var shiftKeyView: TextView? = null
    private var modeKeyView: TextView? = null

    // ── Colours / dimensions ──────────────────────────────────────────────────
    private val keyBg         = ContextCompat.getColor(context, R.color.key_background)
    private val keyFnBg       = ContextCompat.getColor(context, R.color.key_fn_background)
    private val keyboardBg    = ContextCompat.getColor(context, R.color.keyboard_background)
    private val keyText       = ContextCompat.getColor(context, R.color.key_text)
    private val keyFnText     = ContextCompat.getColor(context, R.color.key_fn_text)
    private val keyReturnBg   = ContextCompat.getColor(context, R.color.key_return_background)
    private val keyActiveBg   = ContextCompat.getColor(context, R.color.key_active_background)
    private val rowGap        = 4.dp
    private val keyGap        = 4.dp
    private val keyRadius     = 6.dp.toFloat()

    init {
        orientation = VERTICAL
        setBackgroundColor(keyboardBg)
        setPadding(6.dp, 6.dp, 6.dp, 6.dp)
        buildKeyboard()
    }

    // ── Build / rebuild entire keyboard ───────────────────────────────────────

    private fun buildKeyboard() {
        removeAllViews()
        keyViews.clear()
        langKeyView = null
        shiftKeyView = null
        modeKeyView = null

        val totalHeightPx = heightDp.dp
        val numRows = when {
            showNumericRow -> 5   // numeric + 3 alpha/sym + bottom
            else -> 4
        }
        val rowHeight = (totalHeightPx - (numRows - 1) * rowGap - paddingTop - paddingBottom) / numRows

        if (showNumericRow) {
            addRow(KeyboardData.NUMERIC_ROW, rowHeight, isFn = true)
        }

        val mainRows = when (mode) {
            Mode.QWERTY -> KeyboardData.QWERTY
            Mode.SYMBOLS_1 -> KeyboardData.SYMBOLS_PAGE_1
            Mode.SYMBOLS_2 -> KeyboardData.SYMBOLS_PAGE_2
        }
        for (row in mainRows) {
            addRow(row, rowHeight, isFn = false)
        }

        val bottomRow = when (mode) {
            Mode.QWERTY -> KeyboardData.BOTTOM_ROW
            else -> KeyboardData.SYMBOLS_BOTTOM_ROW
        }
        addRow(bottomRow, rowHeight, isFn = true)
    }

    private fun addRow(keys: List<KeyDef>, rowHeightPx: Int, isFn: Boolean) {
        val row = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
        }
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, rowHeightPx).apply {
            topMargin = if (childCount == 0) 0 else rowGap
        }
        addView(row, lp)

        val totalWeight = keys.sumOf { it.widthWeight.toDouble() }.toFloat()

        for (key in keys) {
            val tv = buildKeyView(key, isFn, totalWeight)
            row.addView(tv)

            if (key.type == KeyType.LANGUAGE) langKeyView = tv
            if (key.type == KeyType.SHIFT) shiftKeyView = tv
            if (key.type == KeyType.MODE) modeKeyView = tv
        }

        refreshKeyLabels()
    }

    // ── Build a single key TextView ───────────────────────────────────────────

    private fun buildKeyView(key: KeyDef, isFn: Boolean, totalWeight: Float): TextView {
        val tv = TextView(context)
        val klp = LayoutParams(0, LayoutParams.MATCH_PARENT, key.widthWeight).apply {
            leftMargin = keyGap / 2
            rightMargin = keyGap / 2
        }
        tv.layoutParams = klp
        tv.gravity = Gravity.CENTER
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (key.type == KeyType.SPACE) 13f else 17f)
        tv.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        tv.setTextColor(if (isFn || key.type != KeyType.CHAR) keyFnText else keyText)
        tv.setBackgroundResource(
            when (key.type) {
                KeyType.RETURN -> R.drawable.key_rounded_return
                KeyType.CHAR, KeyType.SPACE -> R.drawable.key_rounded
                else -> R.drawable.key_rounded_fn
            }
        )
        tv.elevation = 2.dp.toFloat()
        tv.isClickable = true
        tv.isFocusable = false
        tv.tag = key

        tv.setOnClickListener { handleKeyTap(key, tv) }
        tv.setOnLongClickListener {
            if (key.type == KeyType.BACKSPACE) {
                deleteWord(); true
            } else false
        }

        keyViews.add(tv)
        return tv
    }

    // ── Key tap handler ───────────────────────────────────────────────────────

    private fun handleKeyTap(key: KeyDef, view: TextView) {
        triggerHaptic()
        when (key.type) {
            KeyType.CHAR -> {
                val ch = if (mode == Mode.QWERTY && (shiftOn || capsLock))
                    key.label.uppercase()
                else
                    key.label
                listener.onCharKey(ch)
                if (shiftOn && !capsLock) {
                    shiftOn = false
                    refreshKeyLabels()
                }
            }
            KeyType.BACKSPACE -> listener.onBackspace()
            KeyType.RETURN -> listener.onReturn()
            KeyType.SPACE -> listener.onSpace()
            KeyType.SHIFT -> handleShiftTap()
            KeyType.MODE -> handleModeTap(key)
            KeyType.LANGUAGE -> {
                listener.onLanguageToggle()
            }
            KeyType.DISMISS -> listener.onDismiss()
        }
    }

    // ── Shift logic (single-tap = one-shot, double-tap = caps-lock) ──────────

    private fun handleShiftTap() {
        val now = System.currentTimeMillis()
        if (now - lastShiftTap < 400) {
            // Double-tap → caps lock toggle
            capsLock = !capsLock
            shiftOn = capsLock
        } else {
            if (capsLock) {
                capsLock = false
                shiftOn = false
            } else {
                shiftOn = !shiftOn
            }
        }
        lastShiftTap = now
        refreshKeyLabels()
    }

    // ── Mode cycle: QWERTY → Sym1 → Sym2 (or back) ───────────────────────────

    private fun handleModeTap(key: KeyDef) {
        mode = when {
            key.label == "ABC" -> Mode.QWERTY
            key.label == "!#1" -> Mode.SYMBOLS_1
            key.label == "pg1" -> Mode.SYMBOLS_1
            key.label == "pg2" -> Mode.SYMBOLS_2
            else -> Mode.QWERTY
        }
        buildKeyboard()
    }

    // ── Refresh labels after shift/caps/language change ───────────────────────

    private fun refreshKeyLabels() {
        val shifted = shiftOn || capsLock
        for (tv in keyViews) {
            val key = tv.tag as? KeyDef ?: continue
            when (key.type) {
                KeyType.CHAR -> {
                    tv.text = if (mode == Mode.QWERTY && shifted)
                        key.label.uppercase()
                    else
                        key.label
                }
                KeyType.SHIFT -> {
                    tv.text = when {
                        capsLock -> "⇧⇧"   // indicate locked
                        shiftOn -> "⇧"
                        else -> "⇧"
                    }
                    tv.setBackgroundResource(
                        if (shiftOn || capsLock) R.drawable.key_rounded_active
                        else R.drawable.key_rounded_fn
                    )
                }
                KeyType.LANGUAGE -> {
                    tv.text = if (language == DictionaryManager.Language.ENGLISH) "EN" else "AF"
                }
                KeyType.SPACE -> {
                    tv.text = if (language == DictionaryManager.Language.ENGLISH) "space" else "spasie"
                }
                else -> {} // static labels
            }
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    fun setLanguage(lang: DictionaryManager.Language) {
        language = lang
        refreshKeyLabels()
    }

    /** Called by the IME when it starts receiving input for a new field. */
    fun reset() {
        shiftOn = false
        capsLock = false
        refreshKeyLabels()
    }

    // ── Haptics ───────────────────────────────────────────────────────────────

    private fun triggerHaptic() {
        if (!hapticEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator.vibrate(VibrationEffect.createOneShot(8, 50))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                @Suppress("DEPRECATION")
                v.vibrate(8)
            }
        } catch (_: Exception) { /* vibrator unavailable */ }
    }

    // ── Long-press backspace: delete the entire preceding word ────────────────

    private fun deleteWord() {
        triggerHaptic()
        repeat(10) { listener.onBackspace() }
    }

    // ── dp helper ─────────────────────────────────────────────────────────────

    private val Int.dp: Int
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics
        ).toInt()
}
