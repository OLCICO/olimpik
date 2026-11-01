package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ParentFilterPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("olimpik_spor_prefs", Context.MODE_PRIVATE)

    private val _filterState = MutableStateFlow(loadFilters())
    val filterState: StateFlow<ParentFilterPreferences> = _filterState.asStateFlow()

    private val _themeBranch = MutableStateFlow(prefs.getString(KEY_THEME_BRANCH, "ALL") ?: "ALL")
    val themeBranch: StateFlow<String> = _themeBranch.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean(KEY_DARK_MODE, true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun loadFilters(): ParentFilterPreferences {
        return ParentFilterPreferences(
            branch = prefs.getString(KEY_BRANCH, "Tümü") ?: "Tümü",
            hall = prefs.getString(KEY_HALL, "Tümü") ?: "Tümü",
            ageGroup = prefs.getString(KEY_AGE_GROUP, "Tümü") ?: "Tümü",
            gender = prefs.getString(KEY_GENDER, "Tümü") ?: "Tümü"
        )
    }

    fun saveFilters(filters: ParentFilterPreferences) {
        prefs.edit()
            .putString(KEY_BRANCH, filters.branch)
            .putString(KEY_HALL, filters.hall)
            .putString(KEY_AGE_GROUP, filters.ageGroup)
            .putString(KEY_GENDER, filters.gender)
            .apply()
        _filterState.value = filters
    }

    fun resetFilters() {
        val defaultFilters = ParentFilterPreferences()
        saveFilters(defaultFilters)
    }

    fun setThemeBranch(branch: String) {
        prefs.edit().putString(KEY_THEME_BRANCH, branch).apply()
        _themeBranch.value = branch
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        _isDarkMode.value = enabled
    }

    companion object {
        private const val KEY_BRANCH = "pref_branch"
        private const val KEY_HALL = "pref_hall"
        private const val KEY_AGE_GROUP = "pref_age_group"
        private const val KEY_GENDER = "pref_gender"
        private const val KEY_THEME_BRANCH = "pref_theme_branch"
        private const val KEY_DARK_MODE = "pref_dark_mode"
    }
}
