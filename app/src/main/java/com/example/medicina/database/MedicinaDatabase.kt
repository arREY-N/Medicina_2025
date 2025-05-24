package com.example.medicina.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.medicina.model.*

@Database(
    entities = [
        Medicine::class,
        Category::class,
        MedicineCategory::class],
    version = 5)

abstract class MedicinaDatabase: RoomDatabase() {

    abstract fun medicineDao(): MedicineDao
    abstract fun categoryDao(): CategoryDao
    abstract fun medicineCategoryDao(): MedicineCategoryDao

}