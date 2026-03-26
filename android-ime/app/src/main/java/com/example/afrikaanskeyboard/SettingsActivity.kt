package com.example.afrikaanskeyboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.example.afrikaanskeyboard.databinding.ActivitySettingsBinding

/**
 * SettingsActivity — the launcher activity.
 *
 * When the user first installs the APK it guides them to:
 *   1. Enable the IME in system settings
 *   2. Set it as the default keyboard
 *
 * After that, they can adjust language, keyboard height, haptic feedback, etc.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Load the preference fragment
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(binding.settingsContainer.id, SettingsFragment())
                .commit()
        }

        binding.btnEnableIme.setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }

        binding.btnSetDefault.setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }

        updateSetupState()
    }

    override fun onResume() {
        super.onResume()
        updateSetupState()
    }

    private fun updateSetupState() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val packageName = packageName

        val isEnabled = imm.enabledInputMethodList
            .any { it.packageName == packageName }

        val isDefault = Settings.Secure.getString(
            contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD
        )?.contains(packageName) == true

        binding.statusEnabled.text = if (isEnabled) "✓ Keyboard enabled" else "Keyboard not yet enabled"
        binding.statusDefault.text = if (isDefault) "✓ Set as default" else "Not yet set as default"
        binding.btnEnableIme.isEnabled = !isEnabled
        binding.btnSetDefault.isEnabled = isEnabled && !isDefault
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.keyboard_preferences, rootKey)
        }
    }
}
