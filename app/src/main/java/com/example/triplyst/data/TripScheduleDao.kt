package com.example.triplyst.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import com.example.triplyst.model.TripSchedule

@Dao
interface TripScheduleDao {
    @Query("SELECT * FROM trip_schedule WHERE date = :date")
    fun getSchedulesByDate(date: LocalDate): Flow<List<TripSchedule>>

    @Query("SELECT * FROM trip_schedule ORDER BY date")
    fun getAllSchedules(): Flow<List<TripSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: TripSchedule)

    @Delete
    suspend fun delete(schedule: TripSchedule)
}
