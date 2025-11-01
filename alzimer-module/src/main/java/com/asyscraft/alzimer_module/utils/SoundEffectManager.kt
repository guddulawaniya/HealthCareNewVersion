package com.asyscraft.alzimer_module.utils

import android.content.Context

object SoundEffectManager {
    var isSoundOn: Boolean = true

    fun toggleSound(context: Context) {
        isSoundOn = !isSoundOn
        saveState(context)
    }

    fun saveState(context: Context) {
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            .edit().putBoolean("sound_effect_on", isSoundOn).apply()
    }

    fun loadState(context: Context) {
        isSoundOn = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            .getBoolean("sound_effect_on", true)
    }
}