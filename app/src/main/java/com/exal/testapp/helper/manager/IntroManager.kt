package com.exal.testapp.helper.manager

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntroManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val INTRO_COMPLETED_KEY = "is_intro_completed"
    }

    fun setIntroCompleted() {
        sharedPreferences.edit().putBoolean(INTRO_COMPLETED_KEY, true).apply()
    }

    fun isIntroCompleted(): Boolean {
        return sharedPreferences.getBoolean(INTRO_COMPLETED_KEY, false)
    }

    fun clearIntroFlag() {
        sharedPreferences.edit().remove(INTRO_COMPLETED_KEY).apply()
    }
}