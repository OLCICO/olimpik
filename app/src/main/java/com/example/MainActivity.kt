package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.data.ClubRepository
import com.example.ui.MainScreen
import com.example.ui.theme.OlimpikSporTheme
import com.example.util.PreferencesManager
import com.example.viewmodel.ClubViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            if (com.google.firebase.FirebaseApp.getApps(this).isEmpty()) {
                com.google.firebase.FirebaseApp.initializeApp(this)
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "FirebaseApp init: ${e.message}")
        }

        val preferencesManager = PreferencesManager(applicationContext)
        val clubRepository = ClubRepository(applicationContext)
        val viewModel = ClubViewModel(clubRepository, preferencesManager)

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val themeBranch by viewModel.themeBranch.collectAsState()

            OlimpikSporTheme(
                darkTheme = isDarkMode,
                sportBranch = themeBranch
            ) {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
