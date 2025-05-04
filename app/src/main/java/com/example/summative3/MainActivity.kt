package com.example.summative3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.summative3.ui.theme.Summative3Theme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyDljrHHjxR2d-911e1PognP5Q6HLuyLM6I")
        }
        setContent {
            Summative3Theme {
                val navController = rememberNavController() // Create a NavHostController
                CornerDrawer(navController = navController)             }
        }
    }
}