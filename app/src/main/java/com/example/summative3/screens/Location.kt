package com.example.summative3.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@Composable
fun Location() {
    val context = LocalContext.current
    var location by remember { mutableStateOf<String?>(null) }
    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        context: Context,
        fusedLocationProviderClient: FusedLocationProviderClient,
        onLocationResult: (String) -> Unit
    ) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    onLocationResult("Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                } else {
                    onLocationResult("Could not get last known location.")
                }
            }
        } else {
            onLocationResult("Location permissions not granted.")
        }
    }

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.all { it.value }) {
                Log.d("LocationService", "Location permissions granted")
                getCurrentLocation(context, fusedLocationProviderClient) { currentLocation ->
                    location = currentLocation
                }
            } else {
                location = "Location permissions not granted"
                Log.w("LocationService", "Location permissions not granted")
            }
        }
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            if (locationPermissions.all {
                    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                }) {
                getCurrentLocation(context, fusedLocationProviderClient) { currentLocation ->
                    location = currentLocation
                }
            } else {
                requestLocationPermissionLauncher.launch(locationPermissions)
            }
        }) {
            Text("Get Current Location")
        }
        Text(text = "Current Location: ${location ?: "Waiting for location..."}")
    }
}