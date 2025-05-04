package com.example.summative3

import com.example.summative3.Event
import com.example.summative3.EventDao
import kotlinx.coroutines.flow.Flow

class EventRepository(private val eventDao: EventDao) {

    val allEvents: Flow<List<Event>> = eventDao.getAllEvents()

    suspend fun insertEvent(event: Event) {
        eventDao.insert(event)
    }

    suspend fun deleteEvent(event: Event) {
        eventDao.delete(event)
    }

    suspend fun updateEvent(event: Event) {
        eventDao.update(event)
    }

}