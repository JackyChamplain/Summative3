package com.example.summative3

import android.app.Application
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.summative3.screens.Events
import com.example.summative3.screens.Home
import com.example.summative3.screens.Calculator
import com.example.summative3.screens.MapView
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CornerDrawer(
    navController: NavHostController,
    initialIsMapTouching: Boolean // Use initial state instead of directly changing val
) {
    var isMapTouching by remember { mutableStateOf(initialIsMapTouching) } // Use mutableStateOf for dynamic state

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Disable drawer gestures if the map is being interacted with
    val interactionModifier = if (isMapTouching) {
        Modifier.pointerInput(Unit) {} // Disable touch interactions
    } else {
        Modifier // Enable touch interactions
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Home",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch { drawerState.close() }
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (currentRoute == Routes.Home.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Events",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch { drawerState.close() }
                                navController.navigate(Routes.Events.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (currentRoute == Routes.Events.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Map",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch { drawerState.close() }
                                val defaultMapUrl = "https://www.google.com/maps/place/"
                                val encodedUrl = URLEncoder.encode(defaultMapUrl, "UTF-8")
                                navController.navigate(Routes.MapView.route.replace("{mapUrl}", encodedUrl)) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (currentRoute?.startsWith(Routes.MapView.route.substringBefore("/")) == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Calculator",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch { drawerState.close() }
                                navController.navigate(Routes.Calculator.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (currentRoute == Routes.Calculator.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )

                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Menu") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                },
                content = { paddingValues ->
                    val eventViewModel = EventViewModel(application = LocalContext.current.applicationContext as Application)

                    NavHost(
                        navController = navController,
                        startDestination = Routes.Home.route,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(Routes.Home.route) {
                            Home(navController = navController, eventViewModel = eventViewModel)
                        }
                        composable(Routes.Events.route) {
                            Events(
                                navController = navController,
                                eventViewModel = eventViewModel
                            )
                        }

                        composable(
                            route = Routes.MapView.route,
                            arguments = listOf(navArgument("mapUrl") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val eventViewModel: EventViewModel = viewModel()

                            val mapUrlState = remember { mutableStateOf<String?>(null) }

                            LaunchedEffect(true) {
                                eventViewModel.getFirstEvent { event ->
                                    event?.let {
                                        mapUrlState.value = it.address
                                    } ?: run {
                                        Log.d("EventViewModel", "No event found!")
                                    }
                                }
                            }

                            mapUrlState.value?.let { address ->
                                EventMapView(eventAddress = address, onMapTouch = { isTouching ->
                                    // Update the map interaction state
                                    isMapTouching = isTouching
                                })
                            } ?: run {
                                Text("Loading map...")
                            }
                        }
                        composable(Routes.Calculator.route) {
                            Calculator(navController = navController)
                        }
                    }
                },
                bottomBar = {
                    BottomAppBar {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            IconButton(onClick = {
                                navController.navigate(Routes.Events.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }) {
                                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Events")
                            }

                            IconButton(onClick = {
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }) {
                                Icon(Icons.Filled.Home, contentDescription = "Home")
                            }

                            IconButton(onClick = {
                                val encodedUrl = URLEncoder.encode("https://www.google.com/maps/place/", "UTF-8")
                                navController.navigate("map/$encodedUrl") {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }) {
                                Icon(Icons.Filled.Place, contentDescription = "Map")
                            }

                            IconButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    }
                }
            )
        },
        modifier = interactionModifier // Apply interaction modifier here
    )
}
