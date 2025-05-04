package com.example.summative3

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == Routes.Home.route,
            onClick = {
                navController.navigate(Routes.Home.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.DateRange, contentDescription = "Events") },
            label = { Text("Events") },
            selected = currentRoute == Routes.Events.route,
            onClick = {
                navController.navigate(Routes.Events.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Place, contentDescription = "Map") },
            label = { Text("Map") },
            selected = currentRoute?.startsWith(Routes.MapView.route.substringBefore("/")) == true,
            onClick = {
                val defaultMapUrl = "https://www.google.com/maps/place/"
                navController.navigate(Routes.MapView.route.replace("{mapUrl}", defaultMapUrl)) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}