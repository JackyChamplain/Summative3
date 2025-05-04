package com.example.summative3.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.summative3.Event
import com.example.summative3.EventViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Events(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    eventViewModel: EventViewModel = viewModel()
) {
    val events by eventViewModel.allEvents.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var eventToUpdate by remember { mutableStateOf<Event?>(null) }
    var updatedEventName by remember { mutableStateOf("") }
    var updatedEventDescription by remember { mutableStateOf("") }
    var updatedEventLocation by remember { mutableStateOf("") }
    var updatedSelectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var updatedSelectedTime by remember { mutableStateOf<LocalTime?>(null) }
    val updatedDatePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    val updatedTimePickerState = rememberTimePickerState(is24Hour = false)
    var showTimePicker by remember { mutableStateOf(false) }

    val locationSuggestions = listOf("Home", "Office", "Gym", "Library", "Cafe", "Park")
    var filteredSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var showSuggestions by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Upcoming Events",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )

        LazyColumn {
            items(events) { event ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${event.name}", style = MaterialTheme.typography.titleMedium)
                            Text("${event.date} ${event.time}", style = MaterialTheme.typography.bodyMedium)
                            Text("Location: ${event.address}", style = MaterialTheme.typography.bodySmall)
                        }

                        Row {
                            Button(onClick = {
                                Log.d("EventsScreen", "Deleting event with ID: ${event.id}")
                                eventViewModel.deleteEvent(event)
                            }) {
                                Icon(Icons.Filled.Check, contentDescription = "Done")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                eventToUpdate = event
                                updatedEventName = event.name
                                updatedEventDescription = event.description
                                updatedEventLocation = event.address
                                updatedSelectedDate = LocalDate.parse(event.date)
                                updatedSelectedTime = if (!event.time.contains("AM") && !event.time.contains("PM")) {
                                    // If time does not contain AM or PM, append "AM" (or "PM" if needed)
                                    LocalTime.parse("${event.time} AM", DateTimeFormatter.ofPattern("hh:mm a"))
                                } else {
                                    // If it already contains AM or PM, parse it normally
                                    LocalTime.parse(event.time, DateTimeFormatter.ofPattern("hh:mm a"))
                                }
                                showDialog = true
                            }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        if (showDialog && eventToUpdate != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Edit Event") },
                text = {
                    Column {
                        TextField(
                            value = updatedEventName,
                            onValueChange = { updatedEventName = it },
                            label = { Text("Event Name") }
                        )
                        TextField(
                            value = updatedEventDescription,
                            onValueChange = { updatedEventDescription = it },
                            label = { Text("Description") }
                        )
                        TextField(
                            value = updatedEventLocation,
                            onValueChange = {
                                updatedEventLocation = it
                                filteredSuggestions = locationSuggestions.filter { suggestion ->
                                    suggestion.contains(it, ignoreCase = true)
                                }
                                showSuggestions = filteredSuggestions.isNotEmpty()
                            },
                            label = { Text("Location") }
                        )
                        if (showSuggestions) {
                            DropdownMenu(
                                expanded = true,
                                onDismissRequest = { showSuggestions = false }
                            ) {
                                filteredSuggestions.forEach { suggestion ->
                                    DropdownMenuItem(
                                        text = { Text(suggestion) },
                                        onClick = {
                                            updatedEventLocation = suggestion
                                            showSuggestions = false
                                        }
                                    )
                                }
                            }
                        }
                        TextButton(onClick = { showDatePicker = true }) {
                            Text(updatedSelectedDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "Select Date")
                        }
                        TextButton(onClick = { showTimePicker = true }) {
                            Text(updatedSelectedTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "Select Time")
                        }

                        if (showDatePicker) {
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showDatePicker = false
                                        updatedDatePickerState.selectedDateMillis?.let {
                                            updatedSelectedDate = java.time.Instant.ofEpochMilli(it)
                                                .atZone(java.time.ZoneId.systemDefault())
                                                .toLocalDate()
                                        }
                                    }) {
                                        Text("Confirm")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDatePicker = false }) {
                                        Text("Cancel")
                                    }
                                }
                            ) {
                                DatePicker(state = updatedDatePickerState)
                            }
                        }

                        if (showTimePicker) {
                            AlertDialog(
                                onDismissRequest = { showTimePicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showTimePicker = false
                                        updatedSelectedTime = LocalTime.of(
                                            updatedTimePickerState.hour,
                                            updatedTimePickerState.minute
                                        )
                                    }) {
                                        Text("Confirm")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showTimePicker = false }) {
                                        Text("Cancel")
                                    }
                                },
                                text = {
                                    TimePicker(state = updatedTimePickerState)
                                }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        eventToUpdate?.let {
                            val updatedEvent = it.copy(
                                name = updatedEventName,
                                description = updatedEventDescription,
                                date = updatedSelectedDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: it.date,
                                time = updatedSelectedTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: it.time,
                                address = updatedEventLocation
                            )
                            eventViewModel.updateEvent(updatedEvent)
                            showDialog = false
                        }
                    }) {
                        Text("Update")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
