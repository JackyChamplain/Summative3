package com.example.summative3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import com.example.summative3.screens.Home
import com.example.summative3.screens.Events
import com.example.summative3.screens.MapView

@Composable
fun AppNavGraph(
    navController: NavHostController,
    eventViewModel: EventViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route,
        modifier = modifier
    ) {
        composable(route = Routes.Home.route) {
            Home(navController = navController, eventViewModel = eventViewModel)
        }
        composable(route = Routes.Events.route) {
            Events(navController = navController, eventViewModel = eventViewModel)
        }
        composable(route = Routes.MapView.route) {
            val eventViewModel: EventViewModel = viewModel()
            val context = LocalContext.current

            val firstEventState = remember { mutableStateOf<Event?>(null) }

            LaunchedEffect(Unit) {
                eventViewModel.getFirstEvent { firstEvent ->
                    firstEventState.value = firstEvent
                }
            }

            firstEventState.value?.let { event ->
                EventMapView(eventAddress = event.address)
            } ?: run {
            }
        }

    }
}
