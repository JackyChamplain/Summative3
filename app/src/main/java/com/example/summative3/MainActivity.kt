package com.example.summative3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.summative3.ui.theme.Summative3Theme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fetching API key from local.properties (secured)
        val apiKey = "AIzaSyDljrHHjxR2d-911e1PognP5Q6HLuyLM6I"

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        setContent {
            Summative3Theme {
                val navController = rememberNavController()

                // Managing the state of map interaction
                val isMapTouching = remember { mutableStateOf(false) }

                // Passing the state to CornerDrawer
                CornerDrawer(
                    navController = navController,
                    initialIsMapTouching = isMapTouching.value
                )
            }
        }
    }
}
