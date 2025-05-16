package com.example.summative3

sealed class Routes(val route:String) {
    data object Home: Routes("home")
    data object Events: Routes("events")
    data object MapView : Routes("map/{mapUrl}")
    data object Calculator : Routes("calculator")
}