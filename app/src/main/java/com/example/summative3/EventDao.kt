package com.example.summative3

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: Event)

    @Query("SELECT * FROM events ORDER BY date, time")
    fun getAllEvents(): Flow<List<Event>>

    @Delete
    suspend fun delete(event: Event)

    @Update
    fun update(event: Event)

}