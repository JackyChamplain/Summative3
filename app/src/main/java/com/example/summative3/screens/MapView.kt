package com.example.summative3.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text

@Composable
fun MapView(mapUrl: String) {
    val context = LocalContext.current

    // Launch Google Maps using Intent
    LaunchedEffect(mapUrl) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))
        intent.setPackage("com.google.android.apps.maps") // Try to open in Maps app
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Fallback to browser if Maps app isn't available
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl)))
        }
    }

    // Optionally display a fallback UI while map is opening
    Text("Opening location in Google Maps...")
}
