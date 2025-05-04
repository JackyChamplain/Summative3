package com.example.summative3

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.example.summative3.notification.TaskNotificationService

class EventViewModel(application: Application) : AndroidViewModel(application) {

    // -------- Room DB --------
    private val repository: EventRepository
    val allEvents: Flow<List<Event>>

    // -------- Notification Service --------
    private val notificationService: TaskNotificationService = TaskNotificationService(application.applicationContext)

    init {
        val database = AppDatabase.getDatabase(application)
        val eventDao = database.eventDao()
        repository = EventRepository(eventDao)
        allEvents = repository.allEvents
    }

    fun insertEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertEvent(event)
            notificationService.showTaskNotification(
                taskId = event.id.toLong(),  // Assuming the event has an ID field
                taskTitle = event.name,
                taskDescription = event.description
            )
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEvent(event)
            notificationService.showTaskNotification(
                taskId = event.id.toLong(),
                taskTitle = "Event Deleted",
                taskDescription = "The event has been deleted."
            )
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEvent(event)
            notificationService.showTaskNotification(
                taskId = event.id.toLong(),
                taskTitle = "Updated: ${event.name}",
                taskDescription = "The event has been updated."
            )
        }
    }

    // -------- Address Autocomplete --------
    private val _eventAddress = mutableStateOf(TextFieldValue(""))
    val eventAddress: State<TextFieldValue> = _eventAddress

    private val _autocompletePredictions = mutableStateListOf<AutocompletePrediction>()
    val autocompletePredictions: List<AutocompletePrediction> = _autocompletePredictions

    private var predictionJob: Job? = null
    private val token = AutocompleteSessionToken.newInstance()

    fun updateEventAddress(address: TextFieldValue) {
        _eventAddress.value = address
    }

    fun clearAutocompletePredictions() {
        _autocompletePredictions.clear()
    }

    fun fetchAutocompletePredictions(newText: String, context: Context) {
        predictionJob?.cancel()

        Log.d("Places API", "Searching for: $newText") // Log query text

        if (newText.isNotBlank()) {
            predictionJob = viewModelScope.launch(Dispatchers.IO) {
                delay(300) // debounce
                val placesClient = Places.createClient(context)

                // Create the request for autocomplete predictions
                val request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(newText)
                    .setSessionToken(token)
                    .setTypeFilter(com.google.android.libraries.places.api.model.TypeFilter.ADDRESS)
                    .build()

                Log.d("Places API", "Request built: $request") // Log the request being sent

                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        Log.d("Places API", "Autocomplete predictions found: ${response.autocompletePredictions.size}") // Log number of predictions
                        _autocompletePredictions.clear()
                        _autocompletePredictions.addAll(response.autocompletePredictions)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Places API", "Autocomplete failed: ${exception.message}")
                    }
            }
        } else {
            _autocompletePredictions.clear()
            Log.d("Places API", "Query text is blank, clearing predictions.")
        }
    }

    fun getPlaceDetails(
        prediction: AutocompletePrediction,
        context: Context,
        onAddressSelected: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val placesClient = Places.createClient(context)
            val placeId = prediction.placeId
            val placeFields = listOf(Place.Field.ADDRESS)

            Log.d("Places API", "Fetching details for placeId: $placeId") // Log place ID

            val request = FetchPlaceRequest.builder(placeId, placeFields)
                .setSessionToken(token)
                .build()

            placesClient.fetchPlace(request)
                .addOnSuccessListener { response ->
                    response.place?.address?.let { address ->
                        Log.d("Places API", "Place details found: $address") // Log successful response
                        onAddressSelected(address)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Places API", "Place details fetch failed: ${exception.message}")
                    onAddressSelected(prediction.getFullText(null).toString())
                }
        }
    }

    fun getFirstEvent(onEventRetrieved: (Event?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            allEvents.collect { events ->
                // Get the first event from the list (if available)
                val firstEvent = events.firstOrNull()  // Returns null if the list is empty
                onEventRetrieved(firstEvent)
            }
        }
    }
}
