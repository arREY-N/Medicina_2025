package com.example.medicina.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.medicina.model.Medicine

@Database(entities = [Medicine::class], version = 2)

abstract class MedicinaDatabase: RoomDatabase() {
    abstract fun medicineDao(): MedicineDao

}