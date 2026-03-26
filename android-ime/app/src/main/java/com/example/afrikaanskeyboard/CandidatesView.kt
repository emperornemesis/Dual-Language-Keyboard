package com.example.afrikaanskeyboard

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * CandidatesView — the horizontal word suggestion strip that appears above
 * the keyboard when spell-check detects a possibly incorrect word.
 *
 * Clicking a suggestion replaces the current composing word.
 */
class CandidatesView(
    context: Context,
    private val onSuggestionClick: (String) -> Unit
) : HorizontalScrollView(context) {

    private val container = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(8.dp, 4.dp, 8.dp, 4.dp)
    }

    private val dividerColor = ContextCompat.getColor(context, R.color.divider)
    private val chipBg       = ContextCompat.getColor(context, R.color.key_background)
    private val chipText     = ContextCompat.getColor(context, R.color.accent)
    private val barBg        = ContextCompat.getColor(context, R.color.candidates_background)

    init {
        setBackgroundColor(barBg)
        isHorizontalScrollBarEnabled = false
        addView(container)
    }

    fun setSuggestions(words: List<String>) {
        container.removeAllViews()

        if (words.isEmpty()) {
            visibility = View.GONE
            return
        }

        visibility = View.VISIBLE

        words.forEachIndexed { idx, word ->
            if (idx > 0) addDivider()

            val chip = TextView(context).apply {
                text = word
                setTextColor(chipText)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                setBackgroundResource(R.drawable.key_rounded)
                setPadding(16.dp, 6.dp, 16.dp, 6.dp)
                gravity = Gravity.CENTER
                isClickable = true
                isFocusable = false
                setOnClickListener { onSuggestionClick(word) }
            }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = if (idx == 0) 0 else 4.dp
                rightMargin = 4.dp
            }
            container.addView(chip, lp)
        }
    }

    private fun addDivider() {
        val div = View(context).apply {
            setBackgroundColor(dividerColor)
        }
        container.addView(div, LinearLayout.LayoutParams(1, 28.dp).apply {
            leftMargin = 4.dp
            rightMargin = 4.dp
        })
    }

    private val Int.dp: Int
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics
        ).toInt()
}
