package com.example.summative3.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.summative3.Event
import com.example.summative3.EventViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest

@Composable
fun Home(
    modifier: Modifier = Modifier,
    navController: NavController,
    eventViewModel: EventViewModel
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }

    var newEventName by rememberSaveable { mutableStateOf("") }
    var newEventDescription by rememberSaveable { mutableStateOf("") }
    var newEventAddress by rememberSaveable { mutableStateOf("") }

    var selectedDate by rememberSaveable { mutableStateOf("") }
    var selectedTime by rememberSaveable { mutableStateOf("") }

    var addressSuggestions by remember { mutableStateOf(listOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }

    val datePickerDialog = remember {
        android.app.DatePickerDialog(context).apply {
            setOnDateSetListener { _, year, month, day ->
                selectedDate = "%04d-%02d-%02d".format(year, month + 1, day)
            }
        }
    }

    val timePickerDialog = remember {
        android.app.TimePickerDialog(context, { _, hourOfDay, minute ->
            selectedTime = "%02d:%02d".format(hourOfDay, minute)
        }, 12, 0, true)
    }

    fun fetchAddressSuggestions(query: String) {
        if (query.isEmpty()) {
            addressSuggestions = emptyList()
            return
        }

        isLoading = true
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setSessionToken(token)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions
                addressSuggestions = predictions.map { it.getFullText(null).toString() }
                isLoading = false
            }
            .addOnFailureListener {
                addressSuggestions = listOf("Error fetching suggestions")
                isLoading = false
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (currentRoute != "calculator") {
                            navController.navigate("calculator") {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
    ) {
        Text("Add New Event", style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth()
            .padding(bottom = 16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally))

        OutlinedTextField(
            value = newEventName,
            onValueChange = { newEventName = it },
            label = { Text("Event Name") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        OutlinedTextField(
            value = newEventDescription,
            onValueChange = { newEventDescription = it },
            label = { Text("Event Description") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = newEventAddress,
            onValueChange = {
                newEventAddress = it
                fetchAddressSuggestions(it)
            },
            label = { Text("Event Address") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedButton(
            onClick = { datePickerDialog.show() },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(text = if (selectedDate.isNotBlank()) "Date: $selectedDate" else "Pick a Date")
        }

        OutlinedButton(
            onClick = { timePickerDialog.show() },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(text = if (selectedTime.isNotBlank()) "Time: $selectedTime" else "Pick a Time")
        }

        if (isLoading) CircularProgressIndicator(modifier = Modifier.padding(8.dp))

        if (addressSuggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .background(Color(0xFFEFEFEF))
            ) {
                items(addressSuggestions) { suggestion ->
                    Text(
                        text = suggestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                newEventAddress = suggestion
                                addressSuggestions = emptyList()
                            }
                            .padding(12.dp)
                    )
                    Divider()
                }
            }
        }

        Button(
            onClick = {
                if (newEventName.isNotBlank() && selectedDate.isNotBlank() && selectedTime.isNotBlank()) {
                    val newEvent = Event(
                        name = newEventName,
                        description = newEventDescription,
                        date = selectedDate,
                        time = selectedTime,
                        address = newEventAddress
                    )
                    eventViewModel.insertEvent(newEvent)

                    // Reset inputs
                    newEventName = ""
                    newEventDescription = ""
                    newEventAddress = ""
                    selectedDate = ""
                    selectedTime = ""
                    addressSuggestions = emptyList()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
        ) {
            Text("Save Event")
        }

    }
}
