package com.example.triplyst.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.triplyst.model.Converters
import com.example.triplyst.model.TripSchedule

@Database(entities = [TripSchedule::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripScheduleDao(): TripScheduleDao
}
