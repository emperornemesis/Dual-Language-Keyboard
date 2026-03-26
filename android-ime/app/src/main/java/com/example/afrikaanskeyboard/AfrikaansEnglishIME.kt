package com.example.afrikaanskeyboard

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.preference.PreferenceManager

/**
 * AfrikaansEnglishIME — the core InputMethodService.
 *
 * Lifecycle:
 *   onCreateInputView()       → inflates the keyboard panel
 *   onCreateCandidatesView()  → inflates the word suggestion bar
 *   onStartInputView()        → resets state for each new text field
 *   onCurrentInputChanged()   → updates the input connection reference
 *
 * Key events flow through CustomKeyboardView → this service → InputConnection.
 */
class AfrikaansEnglishIME : InputMethodService(), CustomKeyboardView.KeyListener {

    // ── State ─────────────────────────────────────────────────────────────────
    private var keyboardView: CustomKeyboardView? = null
    private var candidatesView: CandidatesView? = null
    private var currentLanguage = DictionaryManager.Language.ENGLISH
    private var composing = StringBuilder()

    // ── InputMethodService overrides ─────────────────────────────────────────

    override fun onCreateInputView(): View {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val heightDp = prefs.getInt("keyboard_height_dp", 260)
        val showNumericRow = prefs.getBoolean("numeric_row", true)
        val haptic = prefs.getBoolean("haptic_feedback", true)
        val langPref = prefs.getString("active_language", "english")
        currentLanguage = if (langPref == "afrikaans")
            DictionaryManager.Language.AFRIKAANS
        else
            DictionaryManager.Language.ENGLISH

        keyboardView = CustomKeyboardView(
            context = this,
            heightDp = heightDp,
            showNumericRow = showNumericRow,
            hapticEnabled = haptic,
            language = currentLanguage,
            listener = this
        )
        return keyboardView!!
    }

    override fun onCreateCandidatesView(): View {
        candidatesView = CandidatesView(this) { suggestion ->
            applySuggestion(suggestion)
        }
        setCandidatesViewShown(false)
        return candidatesView!!
    }

    override fun onStartInputView(info: EditorInfo, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        composing.clear()
        keyboardView?.reset()
        candidatesView?.setSuggestions(emptyList())
        setCandidatesViewShown(false)
    }

    // ── KeyListener callbacks (from CustomKeyboardView) ───────────────────────

    override fun onCharKey(char: String) {
        val ic = currentInputConnection ?: return
        ic.commitText(char, 1)
        composing.append(char)
        updateCandidates()
    }

    override fun onBackspace() {
        val ic = currentInputConnection ?: return
        if (composing.isNotEmpty()) {
            composing.deleteCharAt(composing.length - 1)
            ic.deleteSurroundingText(1, 0)
            updateCandidates()
        } else {
            ic.deleteSurroundingText(1, 0)
        }
    }

    override fun onReturn() {
        val ic = currentInputConnection ?: return
        val action = currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION)
        if (action != null && action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) {
            ic.performEditorAction(action)
        } else {
            ic.commitText("\n", 1)
        }
        composing.clear()
    }

    override fun onSpace() {
        val ic = currentInputConnection ?: return
        ic.commitText(" ", 1)
        composing.clear()
        candidatesView?.setSuggestions(emptyList())
        setCandidatesViewShown(false)
    }

    override fun onLanguageToggle() {
        currentLanguage = if (currentLanguage == DictionaryManager.Language.ENGLISH)
            DictionaryManager.Language.AFRIKAANS
        else
            DictionaryManager.Language.ENGLISH

        keyboardView?.setLanguage(currentLanguage)
        updateCandidates()

        // Persist the new language preference
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putString("active_language", if (currentLanguage == DictionaryManager.Language.ENGLISH) "english" else "afrikaans")
            .apply()
    }

    override fun onDismiss() {
        requestHideSelf(0)
    }

    // ── Suggestion helpers ────────────────────────────────────────────────────

    private fun updateCandidates() {
        val word = composing.toString().trimEnd()
        if (word.length < 2) {
            setCandidatesViewShown(false)
            candidatesView?.setSuggestions(emptyList())
            return
        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (!prefs.getBoolean("show_spell_check", true)) return

        val suggestions = DictionaryManager.getSuggestions(word, currentLanguage)
        val isWrong = !DictionaryManager.isCorrect(word, currentLanguage)

        if (isWrong || suggestions.isNotEmpty()) {
            candidatesView?.setSuggestions(suggestions)
            setCandidatesViewShown(true)
        } else {
            setCandidatesViewShown(false)
            candidatesView?.setSuggestions(emptyList())
        }
    }

    private fun applySuggestion(word: String) {
        val ic = currentInputConnection ?: return
        val typed = composing.length
        if (typed > 0) {
            ic.deleteSurroundingText(typed, 0)
        }
        ic.commitText("$word ", 1)
        composing.clear()
        candidatesView?.setSuggestions(emptyList())
        setCandidatesViewShown(false)
    }
}
